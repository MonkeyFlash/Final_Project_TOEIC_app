// Dry run: node scripts/import_listening_demo.cjs
// Import: node scripts/import_listening_demo.cjs --apply --firebase-cli-dir /path/to/firebase-tools
const fs = require('node:fs');
const path = require('node:path');
const { isDeepStrictEqual } = require('node:util');
const manifest = require('../data/listening_demo/zenlish_demo.json');
const args = process.argv.slice(2);

function encode(value) {
  if (value === null) return { nullValue: null };
  if (typeof value === 'string') return { stringValue: value };
  if (typeof value === 'boolean') return { booleanValue: value };
  if (Number.isInteger(value)) return { integerValue: String(value) };
  if (Array.isArray(value)) return { arrayValue: { values: value.map(encode) } };
  if (typeof value === 'object') return { mapValue: { fields: fields(value) } };
  throw new Error('Unsupported data type');
}
function fields(value) {
  return Object.fromEntries(Object.entries(value).map(([key, item]) => [key, encode(item)]));
}
function decode(value) {
  if ('stringValue' in value) return value.stringValue;
  if ('integerValue' in value) return Number(value.integerValue);
  if ('booleanValue' in value) return value.booleanValue;
  if ('nullValue' in value) return null;
  if ('arrayValue' in value) return (value.arrayValue.values || []).map(decode);
  if ('mapValue' in value) return Object.fromEntries(
    Object.entries(value.mapValue.fields || {}).map(([key, item]) => [key, decode(item)]));
  throw new Error('Unexpected Firestore field type');
}
function validate() {
  if (manifest.projectId !== 'login-register-toeic' || manifest.databaseId !== '(default)') {
    throw new Error('Unexpected target project/database');
  }
  if (manifest.documents.length !== 4) throw new Error('Expected four demo documents');
  const seen = new Set();
  for (const row of manifest.documents) {
    const d = row.data;
    if (row.collection !== `listening_part${d.part}` || ![1, 2, 3, 4].includes(d.part)
        || !/^demo_[a-zA-Z0-9_]+$/.test(row.documentId) || seen.has(row.collection)) {
      throw new Error('Invalid or duplicate demo target');
    }
    seen.add(row.collection);
    if (!d.demoOnly || d.status !== 'draft' || !d.audioUrl.startsWith('https://')) {
      throw new Error('Expected draft demo with HTTPS audio');
    }
    if (d.part === 1 && !d.imageUrl.startsWith('https://')) throw new Error('Missing Part 1 image');
    const questions = d.part <= 2 ? [d] : d.questions;
    if (!Array.isArray(questions) || questions.length !== d.questionCount
        || (d.part >= 3 && questions.length !== 3)) throw new Error('Invalid question count');
    for (const q of questions) {
      const letters = d.part === 2 ? 'ABC' : 'ABCD';
      if (!letters.includes(q.correctAnswer) || q.correctAnswer.length !== 1
          || !q.answerSourceUrls?.length) throw new Error('Missing answer provenance');
      if (d.part >= 3 && (!q.question || [...letters].some(k => !q[`option${k}`]))) {
        throw new Error('Incomplete grouped question');
      }
    }
  }
}
async function main() {
  validate();
  const root = `projects/${manifest.projectId}/databases/${manifest.databaseId}/documents`;
  console.log(`Target: ${manifest.projectId}; four draft documents / eight questions.`);
  for (const row of manifest.documents) console.log(`${row.collection}/${row.documentId}`);
  if (!args.includes('--apply')) {
    console.log('Dry run passed. No network writes.');
    return;
  }
  const cliIndex = args.indexOf('--firebase-cli-dir');
  if (cliIndex < 0 || !args[cliIndex + 1]) throw new Error('Provide --firebase-cli-dir after firebase login');
  const auth = require(path.resolve(args[cliIndex + 1], 'lib/auth.js'));
  const account = auth.getGlobalDefaultAccount();
  if (!account?.tokens?.refresh_token) throw new Error('Run firebase login first');
  const token = await auth.getAccessToken(account.tokens.refresh_token,
    ['https://www.googleapis.com/auth/cloud-platform']);
  const headers = { Authorization: `Bearer ${token.access_token}`, 'Content-Type': 'application/json' };
  async function request(resource, options = {}) {
    const response = await fetch(`https://firestore.googleapis.com/v1/${resource}`,
      { ...options, headers, signal: AbortSignal.timeout(30000) });
    const body = await response.json();
    if (!response.ok && response.status !== 404) {
      throw new Error(`Firestore HTTP ${response.status}: ${body.error?.status || 'request failed'}`);
    }
    return { response, body };
  }
  const writes = [];
  for (const row of manifest.documents) {
    const name = `${root}/${row.collection}/${row.documentId}`;
    const { response, body } = await request(name);
    if (response.ok) {
      const existing = decode({ mapValue: { fields: body.fields } });
      if (!isDeepStrictEqual(existing, row.data)) throw new Error(`Existing document differs: ${row.collection}/${row.documentId}. No writes made.`);
      console.log(`Already identical: ${row.collection}/${row.documentId}`);
    } else {
      writes.push({ update: { name, fields: fields(row.data) }, currentDocument: { exists: false } });
    }
  }
  if (writes.length) {
    await request(`${root}:commit`, { method: 'POST', body: JSON.stringify({ writes }) });
    console.log(`Created ${writes.length} documents atomically; no existing documents overwritten.`);
  }
  for (const row of manifest.documents) {
    const { response, body } = await request(`${root}/${row.collection}/${row.documentId}`);
    if (!response.ok || !isDeepStrictEqual(decode({ mapValue: { fields: body.fields } }), row.data)) {
      throw new Error(`Read-back verification failed for ${row.collection}`);
    }
  }
  console.log('Verified all four documents by reading them back.');
}
main().catch(error => { console.error(error.message); process.exitCode = 1; });
