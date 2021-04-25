package com.momo.certChain.services;

import com.momo.certChain.model.data.Address;
import com.momo.certChain.repositories.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address createAddress(String street, String city, String province, String postalCode ,String country){
        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setProvince(province);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        return addressRepository.save(address);
    }
}