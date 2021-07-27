import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter } from 'k6/metrics';

export const requests = new Counter('http_reqs');

export const options = {
  stages: [
    { target: 60, duration: '1m' },
    { target: 30, duration: '1m' },
    { target: 15, duration: '1m' },
  ],
  thresholds: {
    requests: ['count < 1000'],
  },
};

export default function () {
  const res = http.get('http://localhost:8080/fruits');

  const checkRes = check(res, {
    'status is 200': (r) => r.status === 200,
  });
}
