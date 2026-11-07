// mockwire(127.0.0.1:8899) 제어. 실행 전 e2e/run.sh가 mockwire를 띄운다.
var base = 'http://127.0.0.1:8899/__mockwire';
function post(path, body) { http.post(base + path, { headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body || {}) }); }
function put(path, body) { http.put(base + path, { headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body || {}) }); }
post('/reset');
post('/rules', { method: 'GET', path: '/api/app/version/check', set: { '$.data.forceUpdate': true, '$.data.latestVersion': '99.0.0' } });
