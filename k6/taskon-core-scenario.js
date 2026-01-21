import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 50 },
    { duration: '60s', target: 100 },
    { duration: '60s', target: 200 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<2000'],
  },
};

const BASE_URL = 'https://api.taskon.co.kr';
const EMAIL = __ENV.EMAIL;
const PASSWORD = __ENV.PASSWORD;

export default function () {
  const loginRes = http.post(
      `${BASE_URL}/api/auth/login`,
      JSON.stringify({ email: EMAIL, password: PASSWORD }),
      { headers: { 'Content-Type': 'application/json' } }
  );

  if (!check(loginRes, { 'login success': r => r.status === 200 })) {
    return;
  }

  const token = loginRes.json('data.accessToken');

  const headers = {
    Authorization: `Bearer ${token}`,
  };

  /* 프로젝트 목록 */
  const projectRes = http.get(`${BASE_URL}/api/projects`, { headers });
  if (!check(projectRes, { 'projects ok': r => r.status === 200 })) return;

  const projectId = projectRes.json('data[0].projectId');
  if (!projectId) return;
  sleep(0.2);

  /* Task 보드 */
  const taskRes = http.get(
      `${BASE_URL}/api/projects/${projectId}/tasks/board`,
      { headers }
  );
  if (!check(taskRes, { 'task board ok': r => r.status === 200 })) return;

  sleep(0.2);

  /* 채팅방 리스트 */
  const chatRoomsRes = http.get(`${BASE_URL}/api/chat/rooms`, { headers });
  if (!check(chatRoomsRes, { 'chat rooms ok': r => r.status === 200 })) return;

  const chatRoomId = chatRoomsRes.json('data[0].chatRoomId');
  if (!chatRoomId) return;

  sleep(0.2);

  /* 채팅 메시지 */
  const msgRes = http.get(
      `${BASE_URL}/api/chat/rooms/${chatRoomId}/messages?page=0&size=20`,
      { headers }
  );

  check(msgRes, { 'chat messages ok': r => r.status === 200 });

  sleep(1);
}
