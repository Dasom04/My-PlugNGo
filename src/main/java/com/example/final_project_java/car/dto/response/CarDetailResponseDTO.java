package com.example.final_project_java.car.dto.response;

import com.example.final_project_java.car.entity.Car;
import com.example.final_project_java.car.entity.CarOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDetailResponseDTO {

   private String id;
   private String carName;
   private String carCompany;
   private int maximumPassenger;
   private Year carYear;
   private int carPrice;
   private String carPicture;
   private CarOptions carOptions;

   // DTO > Entity
   public CarDetailResponseDTO(Car car) {
      this.id = car.getCarId();
      this.carName = car.getCarName();
      this.carCompany = car.getCarCompany();
      this.maximumPassenger = car.getMaximumPassenger();
      this.carYear = car.getCarYear();
      this.carPicture = car.getCarPicture();
      this.carOptions = car.getCarOptions();

   }

}












































