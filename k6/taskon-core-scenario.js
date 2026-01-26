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

/**
 * ✅ VU별 캐시
 */
let token;
let projectId;
let chatRoomId;

export default function () {
  /**
   * 로그인 (VU당 1회만)
   */
  if (!token) {
    const loginRes = http.post(
        `${BASE_URL}/api/auth/login`,
        JSON.stringify({ email: EMAIL, password: PASSWORD }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    const ok = check(loginRes, {
      'login success': r => r.status === 200,
    });

    if (!ok) {
      sleep(1);
      return;
    }

    token = loginRes.json('data.accessToken');
  }

  const headers = {
    Authorization: `Bearer ${token}`,
  };

  /**
   * 프로젝트 목록 (VU당 1회)
   */
  if (!projectId) {
    const projectRes = http.get(`${BASE_URL}/api/projects`, { headers });

    const ok = check(projectRes, {
      'projects ok': r => r.status === 200,
    });

    if (!ok) {
      sleep(1);
      return;
    }

    projectId = projectRes.json('data[0].projectId');
    if (!projectId) {
      sleep(1);
      return;
    }
  }

  sleep(0.2);

  /**
   * Task 보드 (핵심 성능 측정 대상)
   */
  const taskRes = http.get(
      `${BASE_URL}/api/projects/${projectId}/tasks/board`,
      { headers }
  );

  check(taskRes, {
    'task board ok': r => r.status === 200,
  });

  sleep(0.2);

  /**
   * 채팅방 리스트 (VU당 1회)
   */
  if (!chatRoomId) {
    const chatRoomsRes = http.get(`${BASE_URL}/api/chat/rooms`, { headers });

    const ok = check(chatRoomsRes, {
      'chat rooms ok': r => r.status === 200,
    });

    if (!ok) {
      sleep(1);
      return;
    }

    chatRoomId = chatRoomsRes.json('data[0].chatRoomId');
    if (!chatRoomId) {
      sleep(1);
      return;
    }
  }

  sleep(0.2);

  /**
   * 채팅 메시지 조회
   */
  const msgRes = http.get(
      `${BASE_URL}/api/chat/rooms/${chatRoomId}/messages?page=0&size=20`,
      { headers }
  );

  check(msgRes, {
    'chat messages ok': r => r.status === 200,
  });

  sleep(1);
}
