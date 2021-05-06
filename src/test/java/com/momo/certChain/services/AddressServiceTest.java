package com.momo.certChain.services;

import com.momo.certChain.model.data.Address;
import com.momo.certChain.repositories.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @InjectMocks
    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    private final String street = "7894 boul maisonneuve";

    private final String city = "Montreal";

    private final String province="Quebec";

    private final String postalCode="H1X2A2";

    private final String country="Canada";

    @Test
    public void testCreateAddress(){
        Mockito.when(addressRepository.save(any(Address.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Address address = addressService.createAddress(street,city,province,postalCode,country);

        assertEquals(street,address.getStreet());
        assertEquals(city,address.getCity());
        assertEquals(province,address.getProvince());
        assertEquals(postalCode,address.getPostalCode());
        assertEquals(country,address.getCountry());
    }

}