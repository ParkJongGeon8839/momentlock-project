package momentlockdemo.controller.capsule;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import momentlockdemo.entity.Capsule;
import momentlockdemo.entity.Member;
import momentlockdemo.service.AfileService;
import momentlockdemo.service.BoxService;
import momentlockdemo.service.CapsuleService;
import momentlockdemo.service.MemberService;

import momentlockdemo.entity.Afile;
import momentlockdemo.entity.Box;
import momentlockdemo.entity.Capsule;
import momentlockdemo.service.AfileService;
import momentlockdemo.service.CapsuleService;

@Controller("capsuleInsertController")
@RequestMapping("/momentlock")
public class CapsuleInsertController {

    @Autowired
    private CapsuleService capsuleService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BoxService boxService;
    
    @Autowired
    private AfileService afileService;

    // 캡슐 작성 폼 페이지
    @GetMapping("/capsuleinsert")
    public String capsuleinsertPage(@RequestParam("boxid") Long boxid, Model model) {
        model.addAttribute("capsule", new Capsule());
        model.addAttribute("boxid", boxid);
        return "html/capsule/capsuleinsert";
    }

    /*
    캡슐 저장 + AWS S3 다중 파일 업로드 + 랜덤 썸네일 부여
    */
    @PostMapping("/capsuleinsert")
    public String capsuleinsert(
            @ModelAttribute("capsule") Capsule capsule,
            @RequestParam("boxid") Long boxid,
            @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {

        //  (선택) 로그인 사용자 연결
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Member member = memberService.getMemberByUsername(username)
//                .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));
//        capsule.setMember(member);

    	Box box = boxService.getBoxById(boxid)
                .orElseThrow(() -> new IllegalArgumentException("박스 정보를 찾을 수 없습니다."));
        capsule.setBox(box);
    	
        // 캡슐 썸네일 랜덤 색상 지정
        int randomNum = (int) (Math.random() * 6) + 1;  // 1~10
        capsule.setCapImage("capsule" + randomNum + ".png");

        //  캡슐 DB 저장
        Capsule savedCapsule = capsuleService.insertCapsule(capsule);

        //  여러 파일 S3 업로드
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    afileService.saveFileToCapsule(file, savedCapsule);
                }
            }
        }

        //  등록 후 리다이렉트
        return "redirect:/momentlock/boxdetail?boxid=" + savedCapsule.getBox().getBoxid();
    }



 //  캡슐 수정 폼 페이지
    @GetMapping("/capsuleupdate")
    public String capsuleupdatePage(@RequestParam("capid") Long capid, Model model) {
        Capsule capsule = capsuleService.getCapsuleById(capid)
                .orElseThrow(() -> new IllegalArgumentException("해당 캡슐을 찾을 수 없습니다. ID=" + capid));

        model.addAttribute("capsule", capsule);
        model.addAttribute("boxid", capsule.getBox().getBoxid()); //  수정 시에도 boxid 전달
        return "html/capsule/capsuleupdate";
    }

    //  캡슐 수정 처리
    @PostMapping("/capsuleupdate")
    public String updateCapsule(
            @ModelAttribute("capsule") Capsule capsule,
            @RequestParam("boxid") Long boxid, //  form에 hidden으로 boxid도 같이 전달
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) throws IOException {

        //  기존 캡슐 조회
        Capsule existing = capsuleService.getCapsuleById(capsule.getCapid())
                .orElseThrow(() -> new IllegalArgumentException("해당 캡슐을 찾을 수 없습니다."));

        //  로그인 사용자 연결
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.getMemberByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));
        existing.setMember(member);

        // 박스 연결
        Box box = boxService.getBoxById(boxid)
                .orElseThrow(() -> new IllegalArgumentException("박스 정보를 찾을 수 없습니다."));
        existing.setBox(box);

        //  캡슐 내용 수정
        existing.setCaptitle(capsule.getCaptitle());
        existing.setCapcontent(capsule.getCapcontent());

        Capsule updatedCapsule = capsuleService.insertCapsule(existing);

        //  새 파일 업로드 있을 경우 추가 저장
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    afileService.saveFileToCapsule(file, updatedCapsule);
                }
            }
        }

        //  수정 후 다시 해당 박스 상세로 리다이렉트
        return "redirect:/momentlock/boxdetail?boxid=" + boxid;
    }

}
