//package kr.co.ohgoodfood._legacy.dao;
//
//import kr.co.ohgoodfood._legacy.dto.MainStoreDTO;
//import kr.co.ohgoodfood._legacy.dto.UserMainFilter;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.List;
//
///**
// * UserMainMapper
// *
// * 사용자 main 페이지에서 사용하는 mapper interface
// */
//
//@Mapper
//public interface UserMainMapper {
//    /**
//     * 사용자 메인 화면 영역에 표시할 가게 목록을 조회
//     *
//     * @param userMainFilter   필터링 정보가 들어간 DTO (카테고리, 픽업 날짜, 스토어 오픈 여부, 위도, 경도, [검색] (가게 이름, 가게 대표 메뉴, 가게 상세))
//     * @return                 필터 적용된 MainStore 리스트
//     */
//    List<MainStoreDTO> selectAllStore(@Param("filter") UserMainFilter userMainFilter);
//
//    /**
//     * 사용자 지도 영역에 핀을 클릭했을때 가게 정보 조회
//     * 고른 가게의 id로 select 하는 것이므로, 위도 경도 정보나 검색 필터링 값들은 필요없다.
//     *
//     * @param userMainFilter   필터링 정보가 들어간 DTO (카테고리, 가게 id, 픽업 날짜, 스토어 오픈 여부, [검색] (가게 이름, 가게 대표 메뉴, 가게 상세))
//     * @return                 필터 적용된 MainStore 요소
//     */
//    MainStoreDTO selectOneStoreByStoreId(@Param("filter") UserMainFilter userMainFilter);
//}
