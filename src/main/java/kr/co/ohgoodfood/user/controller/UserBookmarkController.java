package kr.co.ohgoodfood.user.controller;

import jakarta.validation.Valid;
import kr.co.ohgoodfood._legacy.dto.*;
import kr.co.ohgoodfood.user.service.UserBookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * UserBookmarkController
 *
 * 사용자 bookmark 페이지 전용 기능을 처리하는 컨트롤러입니다.
 * - GET  /user/bookmark              : 해당 user_id가 가진 bookmark 목록 조회
 * - POST /user/bookmark/delete       : 해당하는 bookmark 삭제
 * - POST /user/bookmark/insert       : 해당하는 bookmark 추가
 */

@Controller
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserBookmarkController {
    private final UserBookmarkService userBookmarkService;

    /**
     * 해당 user가 가진 북마크 리스트를 조회한다.
     *
     * @param model         뷰에 전달할 데이터(Model)
     * @param session       현재 HTTP 세션(로그인된 사용자 정보)
     * @return              users/userBookmark.jsp로 포워딩
     */
    @GetMapping("/bookmark")
    public String userBookmark(Model model,
                               HttpSession session) {
        //세션에서 받아오는 로직
        Account loginUser = (Account) session.getAttribute("user");
        String user_id = loginUser.getUser_id();

        List<BookmarkDTO> bookmarkList = userBookmarkService.getBookmarkList(user_id);
        model.addAttribute("bookmarkList", bookmarkList);

        return "users/userBookmark"; // /WEB-INF/views/users/userBookmark.jsp로 forwarding
    }

    /**
     * 해당 user가 가진 북마크 리스트 중, 특정 북마크를 삭제한다.
     *
     * @param bookmarkFilter bookMark delete에 필요한 필드 정보가 담긴 DTO (store_id, user_id)
     * @param session        현재 HTTP 세션(로그인된 사용자 정보)
     * @return               json 응답, 성공시 {"code" : 200} / 실패시 {"code" : 500} / 유효성 검사 실패시 {"code" : 400}
     */
    @PostMapping("/bookmark/delete")
    @ResponseBody //json으로 code응답을 주기 위함이다.
    public Map<String, Integer> userBookmarkDelete(@Valid @RequestBody BookmarkFilter bookmarkFilter,
                                                   BindingResult br,
                                                   HttpSession session) {
        //세션에서 받아오는 로직
        Account loginUser = (Account) session.getAttribute("user");
        String user_id = loginUser.getUser_id();

        //bookmark를 위해 user_id 세팅
        bookmarkFilter.setUser_id(user_id);

        //유효성 검사 필드가 하나 이므로, 단일 구성
        if (br.hasFieldErrors("store_id")) {
            return Collections.singletonMap("code", 400);
        }

        //delete bookmark 실행
        boolean result = userBookmarkService.deleteUserBookMark(bookmarkFilter);
        return Collections.singletonMap("code", result ? 200 : 500);
    }

    /**
     * 해당 user가 가진 북마클 리스트에서 삭제 된 것을 다시 추가하기 위함이다.
     *
     * @param bookmarkFilter bookMark delete에 필요한 필드 정보가 담긴 DTO (store_id, user_id)
     * @param session        현재 HTTP 세션(로그인된 사용자 정보)
     * @return               json 응답, 성공시 {"code" : 200} / 실패시 {"code" : 500} / 유효성 검사 실패시 {"code" : 400}
     */
    @PostMapping("/bookmark/insert")
    @ResponseBody //json으로 code응답을 주기 위함이다.
    public Map<String, Integer> userBookmarkInsert (@Valid @RequestBody BookmarkFilter bookmarkFilter,
                                                    BindingResult br,
                                                    HttpSession session){
        //세션에서 받아오는 로직
        Account loginUser = (Account) session.getAttribute("user");
        String user_id = loginUser.getUser_id();

        //bookmark를 위해 user_id 세팅
        bookmarkFilter.setUser_id(user_id);

        //유효성 검사 필드가 하나 이므로, 단일 구성
        if (br.hasFieldErrors("store_id")) {
            return Collections.singletonMap("code", 400);
        }

        //delete bookmark 실행
        boolean result = userBookmarkService.insertUserBookMark(bookmarkFilter);
        return Collections.singletonMap("code", result ? 200 : 500);
    }
}
