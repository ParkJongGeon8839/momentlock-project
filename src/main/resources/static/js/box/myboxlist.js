let boxId = null;

document.addEventListener("DOMContentLoaded", function() {
   const menuButtons = document.querySelectorAll(".menu-btn");

   menuButtons.forEach(btn => {
      btn.addEventListener("click", function(e) {
         e.stopPropagation(); // 클릭이 바깥으로 퍼지는 걸 방지
         const dropdown = btn.nextElementSibling;

         // 다른 열려있는 드롭다운 닫기
         document.querySelectorAll(".dropdown").forEach(d => {
            if (d !== dropdown) d.style.display = "none";
         });

         // 현재 버튼의 드롭다운 토글
         dropdown.style.display = (dropdown.style.display === "block") ? "none" : "block";
      });
   });

   // 바깥 클릭 시 모든 드롭다운 닫기
   document.addEventListener("click", function() {
      document.querySelectorAll(".dropdown").forEach(d => d.style.display = "none");
   });
});


// ✅ "상자 보내기" 클릭 시 폼 열기 (드롭다운의 3번째 a 태그 기준)
document.querySelectorAll(".dropdown a:nth-child(3)").forEach(sendBtn => {
   sendBtn.addEventListener("click", function(e) {
      e.preventDefault();
      const sendForm = document.getElementById("send-form");
      if (sendForm) sendForm.style.display = "flex";

      const boxCard = e.target.closest(".box_card");

      // 상대에게 보낼 상자의 아이디
      boxId = boxCard.querySelector(".boxid").value;
      console.log("boxid= " + boxId);

   });
});


document.querySelector(".send-submit").addEventListener('click', () => {
   const inputNickname = document.querySelector('.send-nickname').value.trim();
   transmit(boxId, inputNickname);
})

// 박스 보내기
async function transmit(boxId, inputNickname) {

   const url =
      `/momentlock/boxTransmit?boxid=${boxId}&inputNickname=${inputNickname}`;
   console.log('요청한 url=> ' + url);

   const response = await fetch(url);
   console.log(response);
   const status = response.status;

   if (status != 200) {
      console.log("status=> " + status);
      location.href = `/error/${status}.html`;
   }


   const userExists = await response.text();
   console.log('유저 존재 여부=> ' + userExists);

   if (!userExists) {
      alert('해당 유저가 존재하지 않습니다.');
      return;
   } else {
      alert('상자를 보냈습니다!');
      return;
   }

}

// ✅ 닫기 버튼
const sendCloseBtn = document.querySelector(".send-close");
if (sendCloseBtn) {
   sendCloseBtn.addEventListener("click", function() {
      const sendForm = document.getElementById("send-form");
      if (sendForm) sendForm.style.display = "none";
   });
}


// 상자 오픈 이미지
const boxCards = document.querySelectorAll(".box_card");

function updateBoxes() {
   const now = new Date();

   boxCards.forEach(card => {
      const dateElem = card.querySelector(".box-open-date");
      const imgElem = card.querySelector(".box");

      if (dateElem && imgElem) {
         const openDateStr = dateElem.dataset.opendate;
         if (!openDateStr) return;

         const openDate = new Date(openDateStr);
         const remainingMs = openDate - now;

         if (remainingMs <= 0) {
            // 🔹 이미지가 다를 때만 변경
            if (!imgElem.src.includes("myboxlistopen.png")) {
               imgElem.src = "/img/myboxlistopen.png";
            }
            dateElem.textContent = "열림";
         } else {
            // 🔹 이미지가 다를 때만 변경
            if (!imgElem.src.includes("myboxlist.png")) {
               imgElem.src = "/img/myboxlist.png";
            }

            const days = Math.floor(remainingMs / (1000 * 60 * 60 * 24));
            const hours = Math.floor((remainingMs / (1000 * 60 * 60)) % 24);
            const minutes = Math.floor((remainingMs / (1000 * 60)) % 60);
            const seconds = Math.floor((remainingMs / 1000) % 60);

            dateElem.textContent = `${days}일 ${hours}시 ${minutes}분 ${seconds}초`;
         }
      }
   });
}

// 🔁 1초마다 갱신
setInterval(updateBoxes, 1000);
updateBoxes();




// 삭제 버튼 클릭 시 확인창
document.querySelectorAll(".dropdown a:nth-child(2)").forEach(deleteBtn => {
   deleteBtn.addEventListener("click", function(e) {
      e.preventDefault();

      // 상자 카드 요소 찾기
      const boxCard = deleteBtn.closest(".box_card");
      const boxNameElem = boxCard.querySelector("h2");
      const boxName = boxNameElem ? boxNameElem.textContent : "이 상자";

      const confirmDelete = confirm(`정말 "${boxName}" 상자를 삭제하시겠습니까?`);
      if (confirmDelete) {
         // 확인하면 원래 링크로 이동
         window.location.href = deleteBtn.href;
      }
   });
});

// 상자 이미지 클릭 이벤트
document.querySelectorAll('.box_card .box').forEach(img => {
   img.addEventListener('click', function() {
      const boxCard = this.closest('.box_card');
      const boxName = boxCard.querySelector('h2').textContent;
      const boxId = boxCard.querySelector('.boxid').value;

      if (confirm(`${boxName}의 상세 페이지로 이동하시겠습니까?`)) {
         location.href = `/momentlock/boxdetail?boxid=${boxId}`;
      }
   });
});