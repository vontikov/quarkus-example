import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter } from 'k6/metrics';

export const requests = new Counter('http_reqs');

export const options = {
  stages: [
    { target: 20, duration: '3m' },
    { target: 15, duration: '3m' },
    { target: 10, duration: '3m' },
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
