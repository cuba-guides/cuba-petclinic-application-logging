package com.haulmont.sample.petclinic.service;

import com.haulmont.sample.petclinic.entity.pet.Pet;
import com.haulmont.sample.petclinic.entity.pet.PetType;
import com.haulmont.cuba.core.app.EmailerAPI;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.EmailInfo;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(DiseaseWarningMailingService.NAME)
public class DiseaseWarningMailingServiceBean implements DiseaseWarningMailingService {


  private static final Logger log = LoggerFactory.getLogger(DiseaseWarningMailingServiceBean.class);

  @Inject
  protected DataManager dataManager;


  @Inject
  protected EmailerAPI emailerAPI;


  @Override
  public int warnAboutDisease(PetType petType, String disease, String city) {

    log.debug("Disease warnings should be send out for Pet type: {}, Disease: {} in the area of {}", petType, disease, city);

    List<Pet> petsInDiseaseCity = findPetsInDiseaseCity(petType, city);
    List<Pet> petsWithEmail = filterPetsWithValidOwnersEmail(petsInDiseaseCity);

    log.debug("possible pets in danger: {}", petsWithEmail);

    petsWithEmail.forEach(pet -> sendEmailToPetsOwner(pet, disease, city));
    int amountOfInformedPets = petsWithEmail.size();

    log.info("Summary: Disease warning send out to {} Pet(s) in {}", amountOfInformedPets, city);

    return amountOfInformedPets;
  }

  private List<Pet> filterPetsWithValidOwnersEmail(List<Pet> petsInDiseaseCity) {
    return petsInDiseaseCity
        .stream()
        .filter(pet -> !StringUtils.isEmpty(pet.getOwner().getEmail()))
        .collect(Collectors.toList());
  }

  private void sendEmailToPetsOwner(Pet pet, String disease, String city) {
    String emailSubject = "Warning about " + disease + " in the Area of " + city;

    Map<String, Serializable> templateParameters = getTemplateParams(disease, city, pet);

    String ownerEmail = pet.getOwner().getEmail();

    EmailInfo email = new EmailInfo(
        ownerEmail,
        emailSubject,
        null,
        "com/haulmont/sample/petclinic/templates/disease-warning-mailing.txt",
        templateParameters
    );

    emailerAPI.sendEmailAsync(email);

    log.debug("Disease Warning Email send to {} for pet {}", ownerEmail, pet);
  }

  private List<Pet> findPetsInDiseaseCity(PetType petType, String city) {
    return dataManager.load(Pet.class)
        .query(
            "select e from petclinic_Pet e where e.owner.city = :ownerCity and e.type = :petType")
        .parameter("ownerCity", city)
        .parameter("petType", petType)
        .view("pet-with-owner-and-type")
        .list();
  }

  private Map<String, Serializable> getTemplateParams(String disease, String city, Pet pet) {
    Map<String, Serializable> templateParameters = new HashMap<>();

    templateParameters.put("owner", pet.getOwner());
    templateParameters.put("pet", pet);
    templateParameters.put("disease", disease);
    templateParameters.put("city", city);
    return templateParameters;
  }

}