package kr.co.ohgoodfood.user.service;

import kr.co.ohgoodfood._legacy.dto.MainStoreDTO;
import kr.co.ohgoodfood._legacy.dto.UserMainFilter;
import java.util.List;

/**
 * UserMainService interface
 * - UsersService 기능 틀 interface
 * - 유지 보수 편의성 및, DIP 원칙을 준수하기 위해 interface로 구성한다.
 */

public interface UserMainService {
    //[Controller 로직] 메인화면 Controller 연결 로직
    public List<MainStoreDTO> getMainStoreList(UserMainFilter userMainFilter);

    //[Controller 로직] Map에서 클릭한 pin 정보 Controller 연결 로직
    public MainStoreDTO getMainStoreOne(UserMainFilter userMainFilter);
}
