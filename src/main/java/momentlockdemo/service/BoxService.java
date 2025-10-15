package momentlockdemo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import momentlockdemo.dto.BoxLikeCountDto;
import momentlockdemo.entity.Box;

public interface BoxService {

	// 기본 CRUD
	public abstract Box createBox(Box box);

	public abstract Optional<Box> getBoxById(Long boxid);

	public abstract List<Box> getAllBoxes();

	public abstract Box updateBox(Box box);

	public abstract void deleteBox(Long boxid);

	// 추가 비즈니스 로직
	public abstract List<Box> searchBoxByName(String boxname);

	public abstract Optional<Box> getBoxByInviteCode(String boxinvitecode);

	public abstract List<Box> searchBoxByLocation(String boxlocation);

	public abstract List<Box> getBoxesOpenBefore(LocalDateTime date);

	public abstract List<Box> getBoxesOpenAfter(LocalDateTime date);

	public abstract Optional<Box> getBoxByBuryCode(String boxburycode);
	
	public abstract Page<BoxLikeCountDto> getPagedPopularBox(int currPage, int size);
	
	public abstract Page<Box> getPagedBoxList(int currPage, int size);
	
	public abstract Page<Box> getAllBoxPage(Pageable pageable);

}
