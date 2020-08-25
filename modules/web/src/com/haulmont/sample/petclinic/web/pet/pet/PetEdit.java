package com.haulmont.sample.petclinic.web.pet.pet;

import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.screen.EditedEntityContainer;
import com.haulmont.cuba.gui.screen.LoadDataBeforeShow;
import com.haulmont.cuba.gui.screen.StandardEditor;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.sample.petclinic.contact.Contact;
import com.haulmont.sample.petclinic.contact.PetContactFetcher;
import com.haulmont.sample.petclinic.entity.pet.Pet;
import java.util.Optional;
import javax.inject.Inject;

@UiController("petclinic_Pet.edit")
@UiDescriptor("pet-edit.xml")
@EditedEntityContainer("petDc")
@LoadDataBeforeShow
public class PetEdit extends StandardEditor<Pet> {

    @Inject
    PetContactFetcher petContactFetcher;

    @Inject
    PetContactDisplay petContactDisplay;


    @Subscribe("fetchContact")
    public void fetchContact(Action.ActionPerformedEvent event) {

        Pet pet = getEditedEntity();

        Optional<Contact> contactInformation = petContactFetcher.findContact(pet);

        petContactDisplay.displayContact(contactInformation, this);
    }
}