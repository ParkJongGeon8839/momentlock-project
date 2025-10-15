// 유효성 검사 함수
function validateInput(inputElement, messageElement, regex, message){
	inputElement.addEventListener("input", e => {
	    messageElement.innerHTML = "";
	    if (!regex.test(e.target.value)) {
	      messageElement.innerHTML = message;
	    }
	 });
}

// 게시판 상세 정보 모달창 함수
function fetchToBoardDetailInfomodal(classname, url){
	const overlay = document.querySelector(".modal-overlay");
	const modal = document.querySelector(".box-modal");
	const closebtn = document.querySelector(".close-btn");
	
	// row 하나 클릭 시 상세 정보 보여주기
	document.querySelectorAll(classname).forEach(row => {
		
		try {
			row.addEventListener("click", async () => {
				const response = 
					await fetch(url	+ row.children[0].textContent, {
						method: "GET"
					})
					
				if(!response.ok){
					throw new Error("상세정보를 찾을 수 없습니다");
				}
				
				const data = await response.json();
				
				if(data.inqtitle != null){
					document.querySelector(".modal-title").innerHTML = data.inqtitle;
					document.querySelector(".modal-content").innerHTML = data.inqcontent;
				} else if(data.title != null){
					document.querySelector(".modal-title").innerHTML = data.title;
					document.querySelector(".modal-content").innerHTML = data.content;
					document.querySelector(".modal-type").innerHTML = data.type;
				}
				
				if(modal && overlay){
					// 모달, 오버레이 display block으로 변경
					modal.style.display = 'block';
					overlay.style.display = 'block';
				}
					
			})
		} catch(err){
			console.log("error", err);
		}
	})
	
	// 모달창이 열려있을 경우 -> 닫기
	if(closebtn != null){
		// x버튼 클릭 시 모달창 닫기
		closebtn.addEventListener("click", () => {
			
			// 모달, 오버레이 display none으로 변경
			modal.style.display = "none";
			overlay.style.display = 'none';
		})
	}
}