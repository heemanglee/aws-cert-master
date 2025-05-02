/**
 * AWS 자격증 덤프 문제 애플리케이션 메인 스크립트
 */

document.addEventListener('DOMContentLoaded', function() {
  // 페이지 로딩 이벤트
  console.log('AWS 자격증 덤프 문제 애플리케이션이 로드되었습니다.');
  
  // 모든 요소에 fadeIn 애니메이션 적용
  document.querySelectorAll('.card, .stat-card').forEach(card => {
    card.classList.add('animate-fade-in');
  });
  
  // 오답 노트 관련 기능
  setupReviewNoteFeatures();
  
  // 문제 풀이 관련 기능
  setupQuestionFeatures();
  
  // 진행률 차트 초기화
  initProgressCharts();
  
  // 모바일 메뉴 토글 버튼
  const navbarToggler = document.querySelector('.navbar-toggler');
  if (navbarToggler) {
    navbarToggler.addEventListener('click', function() {
      console.log('네비게이션 메뉴 토글');
    });
  }
});

/**
 * 오답 노트 관련 기능 초기화
 */
function setupReviewNoteFeatures() {
  // 오답 노트 토글 버튼
  document.querySelectorAll('.toggle-review-btn').forEach(button => {
    button.addEventListener('click', function() {
      const questionId = this.getAttribute('data-question-id');
      const currentFlagged = this.getAttribute('data-flagged') === 'true';
      const newFlagged = !currentFlagged;
      
      toggleReviewFlag(questionId, newFlagged, this);
    });
  });
  
  // 오답 노트 메모 저장 버튼
  document.querySelectorAll('.save-note-btn').forEach(button => {
    button.addEventListener('click', function() {
      const questionId = this.getAttribute('data-question-id');
      const noteElement = document.querySelector(`.review-note[data-question-id="${questionId}"]`);
      if (noteElement) {
        const note = noteElement.value;
        saveReviewNote(questionId, note);
      }
    });
  });
}

/**
 * 오답 노트 플래그 토글 API 호출
 */
function toggleReviewFlag(questionId, flagged, buttonElement) {
  // 로딩 상태 표시
  const originalContent = buttonElement.innerHTML;
  buttonElement.innerHTML = '<span class="loading-spinner"></span> 처리 중...';
  
  fetch('/mypage/review/toggle', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `questionId=${questionId}&flagged=${flagged}`
  })
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      // UI 업데이트
      buttonElement.setAttribute('data-flagged', flagged);
      
      const icon = buttonElement.querySelector('i');
      if (icon) {
        if (flagged) {
          icon.classList.remove('bi-bookmark-plus');
          icon.classList.add('bi-bookmark-check-fill');
          buttonElement.querySelector('span').textContent = '오답 노트에서 제거';
        } else {
          icon.classList.remove('bi-bookmark-check-fill');
          icon.classList.add('bi-bookmark-plus');
          buttonElement.querySelector('span').textContent = '오답 노트에 추가';
        }
      } else {
        // 원래 내용 복원
        buttonElement.innerHTML = originalContent;
      }
      
      // 성공 메시지 표시 (선택적)
      showToast(flagged ? '오답 노트에 추가되었습니다.' : '오답 노트에서 제거되었습니다.', 'success');
    }
  })
  .catch(error => {
    console.error('Error:', error);
    // 오류 메시지 표시
    showToast('처리 중 오류가 발생했습니다.', 'danger');
    // 원래 내용 복원
    buttonElement.innerHTML = originalContent;
  });
}

/**
 * 오답 노트 메모 저장 API 호출
 */
function saveReviewNote(questionId, note) {
  fetch('/mypage/review/note', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `questionId=${questionId}&note=${encodeURIComponent(note)}`
  })
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      // 성공 메시지 표시
      showToast('메모가 저장되었습니다.', 'success');
    }
  })
  .catch(error => {
    console.error('Error:', error);
    // 오류 메시지 표시
    showToast('메모 저장 중 오류가 발생했습니다.', 'danger');
  });
}

/**
 * 문제 풀이 관련 기능 초기화
 */
function setupQuestionFeatures() {
  // 보기 선택 동작 개선
  document.querySelectorAll('.option-item').forEach(option => {
    option.addEventListener('click', function() {
      const input = this.querySelector('input[type="checkbox"], input[type="radio"]');
      if (input) {
        input.checked = !input.checked;
        
        // 선택 효과 적용
        this.classList.toggle('selected', input.checked);
      }
    });
  });
}

/**
 * 진행률 차트 초기화
 */
function initProgressCharts() {
  // 선택적: Chart.js 등의 라이브러리를 사용하여 향상된 차트 구현
  console.log('진행률 차트 초기화');
}

/**
 * 토스트 메시지 표시 함수
 */
function showToast(message, type = 'info') {
  // 이미 토스트 컨테이너가 있는지 확인
  let toastContainer = document.querySelector('.toast-container');
  
  // 없으면 생성
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.className = 'toast-container position-fixed bottom-0 end-0 p-3';
    document.body.appendChild(toastContainer);
  }
  
  // 토스트 ID 생성
  const toastId = 'toast-' + Date.now();
  
  // 토스트 HTML 생성
  const toastHtml = `
    <div id="${toastId}" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="toast-header bg-${type} text-white">
        <strong class="me-auto">알림</strong>
        <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
      <div class="toast-body">
        ${message}
      </div>
    </div>
  `;
  
  // 토스트 추가
  toastContainer.insertAdjacentHTML('beforeend', toastHtml);
  
  // Bootstrap 토스트 초기화
  const toastElement = document.getElementById(toastId);
  const toast = new bootstrap.Toast(toastElement, { autohide: true, delay: 3000 });
  toast.show();
  
  // 자동 제거 (애니메이션 종료 후)
  toastElement.addEventListener('hidden.bs.toast', () => {
    toastElement.remove();
  });
} 