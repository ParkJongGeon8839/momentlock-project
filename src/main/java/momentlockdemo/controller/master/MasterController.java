package momentlockdemo.controller.master;

import java.net.http.HttpClient.Redirect;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import momentlockdemo.entity.Box;
import momentlockdemo.entity.Capsule;
import momentlockdemo.entity.Declaration;
import momentlockdemo.entity.Inquiry;
import momentlockdemo.entity.Member;
import momentlockdemo.entity.master.NoticeQa;
import momentlockdemo.service.BoxService;
import momentlockdemo.service.CapsuleService;
import momentlockdemo.service.DeclarationService;
import momentlockdemo.service.InquiryService;
import momentlockdemo.service.MemberService;
import momentlockdemo.service.NoticeQaService;




@Controller("masterController")
@RequestMapping("/momentlock")
public class MasterController {

	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private InquiryService inquiryService;
	
	@Autowired
	private DeclarationService declarationService;
	
	@Autowired
	private NoticeQaService noticeQaService;
	
	@Autowired
	private BoxService boxService;
	
	@Autowired
	private CapsuleService capsuleService;
	
	
	
	/*
	  관리자
	*/
	
	// 문의게시판
	 @GetMapping("/masterinquirylist")
	 public String masterinquirylistPage(Model model,
			 @PageableDefault(size = 10, sort = "inqid", direction = Sort.Direction.DESC) Pageable pageable) {
	      Page<Inquiry> inquiryPage = inquiryService.getAllInquiries(pageable);
	      model.addAttribute("inquiryPage", inquiryPage);
	      return "html/master/masterinquirylist";
	 }
	
	// 신고게시판
	@GetMapping("/masterdeclarlist")
	public String masterdeclarlistPage(Model model,
			@PageableDefault(size = 10, sort = "decid", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<Declaration> declarationPage = declarationService.getAllDeclarations(pageable);
		model.addAttribute("declarationPage", declarationPage);
		return "html/master/masterdeclarlist";
	}
	
	// 공지사항
	@GetMapping("/masternoticelist")
	public String masternoticelistPage(Model model,
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<NoticeQa> noticeQaPage = noticeQaService.getPageNoticeQa(pageable);
		model.addAttribute("noticeQaPage", noticeQaPage);
		return "html/master/masternoticelist";
	}
	
	// 공지사항/QnA 폼
	@GetMapping("/masterinquiryinsert")
	public String noticeQaForm(Model model) {
		System.out.println(">>>>>>>>>> GET /masterinquiryinsert : noticeQaForm() 메서드 실행됨! <<<<<<<<<<");
		model.addAttribute("noticeQa", new NoticeQa());
		return "html/master/masterinquiryinsert";
	}
	
	//noticeQa 입력폼
	@PostMapping("/masterinquiryinsert")
	public String createNoticeQa(@ModelAttribute("noticeQa") NoticeQa noticeQa) {
		
		 System.out.println(">>>>>>>>>> POST /masterinquiryinsert : createNoticeQa() 메서드 실행됨! <<<<<<<<<<");
		    System.out.println("전달된 제목: " + noticeQa.getTitle());
		    System.out.println("전달된 타입: " + noticeQa.getType());
        noticeQaService.insertNoticeQa(noticeQa);
        
        return "redirect:/momentlock/masternoticelist";
    }
	
	//회원 관리
	@GetMapping("/membermanagement")
	public String membermanagementPage(Model model,
			@PageableDefault(size = 7, sort = "memregdate", direction = Sort.Direction.DESC)Pageable pageable,
			@RequestParam(value = "nickname", required = false)String nickname) {
		Page<Member> memberPage;
		
		if (nickname !=null && !nickname.trim().isEmpty()) {
			memberPage = memberService.getMemberPage(nickname, pageable);
			model.addAttribute("searchKeyword", nickname);
		} else {
			memberPage = memberService.getAllMemberPage(pageable);
		}
        model.addAttribute("memberPage", memberPage);
	    return "html/master/membermanagement";
	}
	
	//회원 삭제
	@PostMapping("/member/updateToMDY")
	public String updateToMDY(@RequestParam("username")String username, RedirectAttributes rttr) {
		try {
			memberService.updateMemberToMDY(username);
			
			rttr.addFlashAttribute("message", username + "회원의 상태가 '탈퇴 요청(MDY)'으로 변경되었습니다." );
		} catch (IllegalArgumentException e) {
			rttr.addFlashAttribute("error", e.getMessage());
		} catch (Exception e) {
			rttr.addFlashAttribute("error", "상태 변경 중 알 수 없는 오류가 발생했습니다.");
		}
		return "redirect:/momentlock/membermanagement";
	}
	
	
	// 상자관리
	@GetMapping("/boxmanagement")
	public String boxmanagementPage(Model model,
			@PageableDefault(size = 7, sort = "boxid", direction = Sort.Direction.DESC)Pageable pageable) {
		Page<Box> boxPage = boxService.getAllBoxPage(pageable);
		model.addAttribute("boxPage", boxPage);
		return "html/master/boxmanagement";
	}
	// 상자삭제
	@PostMapping("/boxes/delete/{boxid}")
	public String deleteBoxM(@PathVariable("boxid") Long boxid, RedirectAttributes redirectAttributes) {
		try {
			boxService.deleteBox(boxid);
			redirectAttributes.addFlashAttribute("message", "상자가 삭제되었습니다.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "삭제 실패:" + e.getMessage());
		}
		return "redirect:/momentlock/boxmanagement";
	}
	
	

	// 타임캡슐 관리
	@GetMapping("/capsulemanagement")
	public String capsulemanagementPage(Model model,
			@PageableDefault(size = 7, sort = "capregdate", direction = Sort.Direction.DESC)Pageable pageable) {
		Page<Capsule> capsulePage = capsuleService.getAllCapsulePage(pageable);
		model.addAttribute("capsulePage", capsulePage);
		return "html/master/capsulemanagement";
	}
	
	//캡슐 삭제
	@PostMapping("/capsule/updateToTDY")
	public String updateToTDY(@RequestParam("capid")Long capid, RedirectAttributes rttr) {
		try {
			capsuleService.updateCapsuleToTDY(capid);
			
			rttr.addFlashAttribute("message", capid + "상자가 '삭제 요청(TDY)'으로 변경되었습니다.");
		} catch (IllegalArgumentException e) {
			rttr.addFlashAttribute("error", e.getMessage());
		} catch (Exception e) {
			rttr.addFlashAttribute("error", "상태 변경 중 알 수 없는 오류가 발생하였습니다.");
		}
		return "redirect:/momentlock/capsulemanagement";
	}
	
	
	// 구독관리
	@GetMapping("/subscriptionmanagement")
	public String subscriptionmanagementPage(Model model,
			@PageableDefault(size = 7, sort = "substartday", direction = Sort.Direction.DESC)Pageable pageable,
			@RequestParam(value = "nickname", required = false)String nickname) {
		Page<Member> subscriptionPage; 
		
		if (nickname !=null && !nickname.trim().isEmpty()) {
			subscriptionPage = memberService.getMemberPage(nickname, pageable);
			model.addAttribute("searchKeyword", nickname);
		} else {
			subscriptionPage = memberService.getAllMemberPage(pageable);
		}
		model.addAttribute("subscriptionPage", subscriptionPage);
		return "html/master/subscriptionmanagement";
	}
	
	// 관리자페이지 메뉴
	@GetMapping("/masterpage")
	public String masterpagePage() {
		return "html/master/masterpage_nav";
	
	}
	
}
