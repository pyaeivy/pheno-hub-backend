package com.example.demo.repository;

import com.example.demo.entity.PhoneBrand;
import com.example.demo.entity.PhoneImage;
import com.example.demo.entity.PhoneModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PhoneRepository {

    private final GenericCriteriaRepository repo;

    public boolean isExistByBrandName(String brandName){
        return repo.exists(PhoneBrand.class, List.of(new SearchCriteria("name", Operator.EQUAL_IGNORE_CASE, brandName)));
    }

    public List<PhoneModel> getModelsByBrand(String brand){
        return repo.find(PhoneModel.class, List.of(new SearchCriteria("brand", Operator.IN, brand)));
    }

    public Optional<PhoneModel> getPhoneModelByName(String name){
        return repo.findOne(PhoneModel.class, List.of(new SearchCriteria("name" , Operator.EQUAL_IGNORE_CASE, name)));
    }

    public Optional<PhoneModel> getPhoneById(Long modelId){
        return repo.findOne(PhoneModel.class, List.of(new SearchCriteria("id", Operator.EQUAL, modelId)));
    }

    public Optional<PhoneBrand> getPhoneBrandByName(String name){
        return repo.findOne(PhoneBrand.class, List.of(new SearchCriteria("name" , Operator.EQUAL_IGNORE_CASE, name)));
    }

    public Optional<PhoneImage> findImageByModel(String modelName){
        return repo.findOne(PhoneImage.class, List.of(new SearchCriteria("modelName", Operator.EQUAL_IGNORE_CASE, modelName)));
    }


}
