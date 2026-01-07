import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * ===============================
 * k6 실행 옵션
 * ===============================
 */
export let options = {
  stages: [
    { duration: '30s', target: 50 },   // 워밍업
    { duration: '60s', target: 200 },  // 일반 트래픽
    { duration: '60s', target: 500 },  // 피크 트래픽
    { duration: '30s', target: 0 },    // 정리
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],        // 실패율 < 1%
    http_req_duration: ['p(95)<1000'],     // P95 < 1초
  },
};

/**
 * ===============================
 * 기본 설정
 * ===============================
 */
const BASE_URL = 'https://api.taskon.co.kr';

const EMAIL = __ENV.EMAIL;
const PASSWORD = __ENV.PASSWORD;

/**
 * ===============================
 * 가상 유저 시나리오
 * ===============================
 */
export default function () {

  /* 1️⃣ 로그인 */
  const loginRes = http.post(
      `${BASE_URL}/api/auth/login`,
      JSON.stringify({
        email: EMAIL,
        password: PASSWORD,
      }),
      { headers: { 'Content-Type': 'application/json' } }
  );

  check(loginRes, {
    'login success (200)': (r) => r.status === 200,
  });

  const accessToken = loginRes.json('data.accessToken');

  const authHeaders = {
    Authorization: `Bearer ${accessToken}`,
  };

  sleep(0.3);

  /* 2️⃣ 프로젝트 목록 조회 */
  const projectRes = http.get(
      `${BASE_URL}/api/projects`,
      { headers: authHeaders }
  );

  check(projectRes, {
    'projects 조회 성공': (r) => r.status === 200,
  });

  const projectId = projectRes.json('data[0].projectId');

  sleep(0.3);

  /* 3️⃣ Task 보드 조회 (핵심 병목 API) */
  const taskBoardRes = http.get(
      `${BASE_URL}/api/projects/${projectId}/tasks/board`,
      { headers: authHeaders }
  );

  check(taskBoardRes, {
    'task board 조회 성공': (r) => r.status === 200,
  });

  sleep(0.3);

  /* 4️⃣ 채팅방 리스트 조회 */
  const chatRoomsRes = http.get(
      `${BASE_URL}/api/chat/rooms`,
      { headers: authHeaders }
  );

  check(chatRoomsRes, {
    'chat rooms 조회 성공': (r) => r.status === 200,
  });

  const chatRoomId = chatRoomsRes.json('data[0].chatRoomId');

  sleep(0.3);

  /* 5️⃣ 채팅 메시지 조회 */
  const messagesRes = http.get(
      `${BASE_URL}/api/chat/rooms/${chatRoomId}/messages?page=0&size=20`,
      { headers: authHeaders }
  );

  check(messagesRes, {
    'chat messages 조회 성공': (r) => r.status === 200,
  });

  sleep(1);
}
