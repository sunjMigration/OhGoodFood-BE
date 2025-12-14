package kr.co.ohgoodfood.notification.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ohgoodfood._legacy.dao.AdminMapper;
import kr.co.ohgoodfood._legacy.dao.ScheduleMapper;
import kr.co.ohgoodfood._legacy.dto.Alarm;
import kr.co.ohgoodfood._legacy.dto.ReservationConfirmed;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

	private final ScheduleMapper scheduleMapper;
    private final AdminMapper adminMapper;


    // 예약 확정 스케쥴드
	@Scheduled(cron = "0 0,30 * * * ?")
	@Transactional
    @Override
	public synchronized void reservationCheck() {

        // 논리적 시간 처리 예정
        Calendar now = Calendar.getInstance();  // 현재 시간
        int minute = now.get(Calendar.MINUTE);

        if (minute < 30) {
            now.set(Calendar.MINUTE, 0);
        } else {
            now.set(Calendar.MINUTE, 30);
        }

        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

		// Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		String formattedDate = sdf.format(now.getTime());

		// 금일 오픈한(예약) 가게 가져오기
		List<ReservationConfirmed> reservationStoreList = scheduleMapper.todayReservation(formattedDate);
		// 금일 오픈한(예약) 가게 상태 업데이트 ( Y --> N )
        // 사장님에게 확정 시간 종료 알람 보내기
		for (ReservationConfirmed store : reservationStoreList) {
			scheduleMapper.updateStoreStatus(store);
            Alarm alarm = new Alarm();
            alarm.setAlarm_title("확정 완료");  
            alarm.setAlarm_contents( "금일 예약이 마감되었습니다.");
            alarm.setReceive_id(store.getStore_id());
            alarm.setAlarm_displayed("Y");
            alarm.setAlarm_read("N");
            adminMapper.sendAlarm(alarm);
		}

		// 금일 예약 가게 주문 가져오기
		List<ReservationConfirmed> reservationOrderList = scheduleMapper.todayReservationOrder(formattedDate);
		// 금일 예약 가게 주문 상태 업데이트 ( reservation --> confirmed )
        // 유저에게 확정 알람 보내기
		for (ReservationConfirmed order : reservationOrderList) {
            order.setOrder_code((int)(Math.random() * 900000) + 100000);
			scheduleMapper.updateOrderStatus(order);
            Alarm alarm = new Alarm();
            alarm.setAlarm_title("확정 완료");  
            alarm.setAlarm_contents(scheduleMapper.getStoreName(order.getStore_id()) + " 예약이 확정되었습니다.");
            alarm.setReceive_id(order.getUser_id());
            alarm.setAlarm_displayed("Y");
            alarm.setAlarm_read("N");
            adminMapper.sendAlarm(alarm);
		}
	}

    // 예약 확정 1시간 전 알림 스케쥴드
    @Scheduled(cron = "0 0,30 * * * ?")
	@Transactional
    @Override
	public synchronized void reservationCheckBeforeOneHour() {
        // 현재 시간 + 1시간
        // 논리적 시간 처리 예정
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        if (minute < 30) {
            calendar.set(Calendar.MINUTE, 0);
        } else {
            calendar.set(Calendar.MINUTE, 30);
        }
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 1시간 더하기
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        Date oneHourLater = calendar.getTime();

        // 문자열 포맷
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        String formattedOneHourLater = sdf.format(oneHourLater);

        // 현재 시간 + 1시간 이후 예약 가게 가져오기
        List<ReservationConfirmed> reservationStoreList = scheduleMapper.todayReservation(formattedOneHourLater);
        // 사장님에게 확정 시간 1시간 전 알람 보내기
        for (ReservationConfirmed store : reservationStoreList) {
            Alarm alarm = new Alarm();
            alarm.setAlarm_title("확정 임박");  
            alarm.setAlarm_contents("확정 마감까지 1시간 남았습니다.");
            alarm.setReceive_id(store.getStore_id());
            alarm.setAlarm_displayed("Y");
            alarm.setAlarm_read("N");
            adminMapper.sendAlarm(alarm);
        }
	}

    // 픽업 시간 종료 스케쥴드
    @Scheduled(cron = "0 0,30 * * * ?")
	@Transactional
    @Override
	public synchronized void pickupCheck() {
		// 논리적 시간 처리 예정
        Calendar now = Calendar.getInstance();  // 현재 시간
        int minute = now.get(Calendar.MINUTE);

        if (minute < 30) {
            now.set(Calendar.MINUTE, 0);
        } else {
            now.set(Calendar.MINUTE, 30);
        }

        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

		// Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		String formattedDate = sdf.format(now.getTime());

        // 픽업 안 된 주문 가져오기
        List<ReservationConfirmed> pickupNotDoneList = scheduleMapper.pickupNotDone(formattedDate);
        // 픽업 안 된 주문 상태 업데이트 ( confirmed --> cancel )
        // 유저에게 픽업 안 된 주문 알람 보내기
        for (ReservationConfirmed order : pickupNotDoneList) {
            // 픽업 안 된 주문 상태 업데이트 ( confirmed --> cancel )
            scheduleMapper.updateOrderStatusCancel(order);
            // 유저에게 픽업 안 된 주문 알람 보내기
            Alarm alarm = new Alarm();
            alarm.setAlarm_title("픽업 종료");  
            alarm.setAlarm_contents(scheduleMapper.getStoreName(order.getStore_id()) + " 픽업 시간이 종료되었습니다.");
            alarm.setReceive_id(order.getUser_id());
            alarm.setAlarm_displayed("Y");
            alarm.setAlarm_read("N");
            adminMapper.sendAlarm(alarm);

            // 사장님에게 픽업 종료 알람 보내기
            Alarm alarm2 = new Alarm();
            alarm2.setAlarm_title("픽업 종료");  
            alarm2.setAlarm_contents(scheduleMapper.getUserNickname(order.getUser_id()) + " 님이 픽업을 하지 않으셨습니다.");
            alarm2.setReceive_id(order.getStore_id());
            alarm2.setAlarm_displayed("Y");
            alarm2.setAlarm_read("N");
            adminMapper.sendAlarm(alarm2);
        }
	}

    //픽업 시작 알람 스케줄드
    @Scheduled(cron = "0 0,30 * * * ?")
	@Transactional
    @Override
	public synchronized void pickupStartCheck() {
		// 논리적 시간 처리 예정
        Calendar now = Calendar.getInstance();  // 현재 시간
        int minute = now.get(Calendar.MINUTE);

        if (minute < 30) {
            now.set(Calendar.MINUTE, 0);
        } else {
            now.set(Calendar.MINUTE, 30);
        }

        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

		// Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		String formattedDate = sdf.format(now.getTime());

        // 픽업 안 된 주문 가져오기, 픽업 시간 시작 기준
        List<ReservationConfirmed> pickupNotDoneStartList = scheduleMapper.pickupNotDoneStart(formattedDate);
        // 유저에게 픽업 안 된 주문 알람 보내기
        for (ReservationConfirmed order : pickupNotDoneStartList) {
            Alarm alarm = new Alarm();
            alarm.setAlarm_title("픽업 시작");  
            alarm.setAlarm_contents(scheduleMapper.getStoreName(order.getStore_id()) + " 픽업이 시작되었습니다.");
            alarm.setReceive_id(order.getUser_id());
            alarm.setAlarm_displayed("Y");
            alarm.setAlarm_read("N");
            adminMapper.sendAlarm(alarm);
        }
	}
}
