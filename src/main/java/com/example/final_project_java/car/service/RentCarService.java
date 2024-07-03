package com.example.final_project_java.car.service;

import com.example.final_project_java.car.dto.request.RentCarRequestDTO;
import com.example.final_project_java.car.dto.request.RentCarResModifyRequestDTO;
import com.example.final_project_java.car.dto.response.RentCarDetailResponseDTO;
import com.example.final_project_java.car.dto.response.RentCarListResponseDTO;
import com.example.final_project_java.car.entity.Car;
import com.example.final_project_java.car.entity.RentCar;
import com.example.final_project_java.car.repository.CarRepository;
import com.example.final_project_java.car.repository.RentCarRepository;
import com.example.final_project_java.userapi.entity.User;
import com.example.final_project_java.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RentCarService {

   private final CarRepository carRepository;
   private final UserRepository userRepository;
   private final RentCarRepository rentCarRepository;

   // 전기차 예약 목록 보기
//   public void getRentList (String userId) {
//      getUserId(userId);
//
//
//
//
////      List<RentCar> entityList = rentCarRepository.findById();
////
////      List<RentCarDetailResponseDTO> dtoList = entityList.stream()
////
////              .map(RentCarDetailResponseDTO::new)
////              .collect(Collectors.toList());
////
////      return RentCarListResponseDTO.builder()
////              .rentList(dtoList)
////              .build();
//   }

   // 한 유저의 전기차 예약 목록을 가져온다.
   public RentCarListResponseDTO getRentList(String userId) {

      User user = getUser(userId);

      List<RentCar> entityList = rentCarRepository.findByUser(userId);
      List<RentCarDetailResponseDTO> dtoList = entityList.stream()
              .map(RentCarDetailResponseDTO::new)
              .collect(Collectors.toList());

      return RentCarListResponseDTO.builder()
              .rentList(dtoList)
              .build();
   }

   // 전기차 예약 상세보기 가져오기
   public RentCarListResponseDTO reservationInfo (int carNo) {

      RentCar reservation = getReservation(carNo);

      RentCarDetailResponseDTO responseDTO = new RentCarDetailResponseDTO(reservation);

      List<RentCarDetailResponseDTO> dtoList = Collections.singletonList(responseDTO);

      return RentCarListResponseDTO.builder()
              .rentList(dtoList)
              .build();
   }

   // 전기차 예약PK로 상세 정보 가져오기
   private RentCar getReservation(int carNo) {
      return rentCarRepository.findById(carNo).orElseThrow(
              () -> new RuntimeException("예약내역이 없습니다." + carNo)
      );
   }


   // 예약하기
   public RentCarDetailResponseDTO create (
           final RentCarRequestDTO requestDTO,
           final String userId,
           final String carId,
           final LocalDateTime rentDate,
           final LocalDateTime turninDate
           ) {
      User user = getUser(userId);
      Car carInfo = getCarInfo(carId);

      // 한 유저가 동일 예약한 날짜에 예약 못하게
      if(rentCarRepository.existsByUserAndRentDateBetween(user, rentDate, turninDate)) {
         throw new IllegalStateException("이미 예약하신 차가 있습니다.");
      } else if(rentCarRepository.existsCar(carId)) {
         throw new IllegalStateException("예약 불가능");
      }

      rentCarRepository.save(requestDTO.toEntity(user, carInfo));
      log.info("차량 예약 완료.");

      return null;
   }

   // 예약 상세
   private Car getCarInfo(String carId) {
      Car car = carRepository.findById(carId).orElseThrow(
              () -> new RuntimeException("차량정보가 존재하지 않습니다.")
      );
      return car;
   }

   // 유저
   private User getUser(String userId) {
      User user = userRepository.findById(userId).orElseThrow(
              () -> new RuntimeException("회원정보가 존재하지 않습니다.")
      );
      return user;
   }


   // 렌트카 예약 삭제
   public RentCarListResponseDTO delete(int carNo, String userId) {
      RentCar deleteReservation = rentCarRepository.findById(carNo).orElseThrow(
              () -> {
                 log.error("예약번호이 조회되지 않아 삭제가 불가능합니다. 예약순서: {}", carNo);
                 throw new RuntimeException("예약번호가 존재하지 않아 삭제에 실패했습니다.");
              }
      );
      rentCarRepository.deleteById(carNo);

      return getRentList(userId);
   }

   // 렌트카 예약 수정
   public RentCarDetailResponseDTO update(RentCarResModifyRequestDTO requestDTO, int carNo) {
      Optional<RentCar> targetEntity = rentCarRepository.findById(carNo);

      targetEntity.ifPresent(reservation -> {
         reservation.setRentTime(requestDTO.getRentTime());
         rentCarRepository.save(reservation);
      });
      return null;
   }



}












































