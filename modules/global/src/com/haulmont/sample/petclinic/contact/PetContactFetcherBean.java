package com.haulmont.sample.petclinic.contact;

import com.haulmont.sample.petclinic.entity.owner.Owner;
import com.haulmont.sample.petclinic.entity.pet.Pet;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.View;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component(PetContactFetcher.NAME)
public class PetContactFetcherBean implements PetContactFetcher {

    private static final Logger log = LoggerFactory.getLogger(PetContactFetcherBean.class);


    @Inject
    DataManager dataManager;

    @Inject
    Messages messages;

    @Override
    public Optional<Contact> findContact(Pet pet) {

        MDC.put("petId", pet.getIdentificationNumber());

        log.debug("Searching Contact for Pet");

        Optional<Owner> petOwner = loadOwnerFor(pet);

        if (petOwner.isPresent()) {
            log.debug("Found Owner: {}", petOwner);

            Owner owner = petOwner.get();
            String telephone = owner.getTelephone();
            String email = owner.getEmail();
            String address = formatOwnerAddress(owner);

            if (isAvailable(telephone)) {
                return createContact(telephone, ContactType.TELEPHONE);
            } else if (isAvailable(email)) {
                return createContact(email, ContactType.EMAIL);
            } else if (isAvailable(address)) {
                return createContact(address, ContactType.ADDRESS);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }

    }


    private Optional<Contact> createContact(String contactValue, ContactType contactType) {

        Contact contact = new Contact();
        contact.setValue(contactValue);
        contact.setType(contactType);

        log.info("Contact created: {}", contact);

        MDC.remove("petId");

        return Optional.of(contact);
    }


    private String formatOwnerAddress(Owner owner) {
        return messages.formatMessage(this.getClass(), "ownerAddressFormat", owner.getFirstName(), owner.getLastName(), owner.getAddress(), owner.getCity());
    }

    private Optional<Owner> loadOwnerFor(Pet pet) {
        if (pet.getOwner() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(dataManager.reload(pet.getOwner(), View.LOCAL));
    }

    private boolean isAvailable(String contactOption) {
        return StringUtils.isNotBlank(contactOption);
    }
}
