package com.nautilus.service.impl;

import com.nautilus.domain.Customer;
import com.nautilus.domain.Packaging;
import com.nautilus.repository.PackagingRepository;
import com.nautilus.service.CustomerService;
import com.nautilus.service.PackagingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PackagingServiceImpl implements PackagingService {

    final
    PackagingRepository packagingRepository;
    final
    CustomerService customerService;

    public PackagingServiceImpl(PackagingRepository packagingRepository, CustomerService customerService) {
        this.packagingRepository = packagingRepository;
        this.customerService = customerService;
    }

    @Override
    public Packaging insert(Packaging obj) {
        updateCustomersObligations(obj.getCustomer(), obj);
        customerService.update(obj.getCustomer());
        return packagingRepository.insert(obj);
    }

    @Override
    public List<Packaging> getAll() {
        return packagingRepository.getAll();
    }

    @Override
    public Packaging findById(Long id) {
        return packagingRepository.findById(id);
    }

    @Override
    public Optional<Packaging> update(Packaging obj) {
        Packaging oldPackaging = this.findById(obj.getId());
        packagingRepository.update(obj);
        revertCustomersObligations(obj.getCustomer(), oldPackaging);
        updateCustomersObligations(obj.getCustomer(), obj);
        customerService.update(obj.getCustomer());
        return Optional.of(obj);
    }

    @Override
    public void deleteAll(Iterable<Packaging> packaging) {
        packaging.forEach(pack -> {
            revertCustomersObligations(pack.getCustomer(), pack);
            customerService.update(pack.getCustomer());
            packagingRepository.deleteById(pack);
        });
    }

    private void updateCustomersObligations(Customer customer, Packaging packaging) {
        customer.setPackagingSmall(customer.getPackagingSmall() - packaging.getWaterSmallReturned());
        customer.setPackagingLarge(customer.getPackagingLarge() - packaging.getWaterLargeReturned());
        if (packaging.getCustomer().getPackagingSmall() < 0 || packaging.getCustomer().getPackagingLarge() < 0) {
            throw new RuntimeException("Korisnik ne može vratiti više ambalaže nego što je zadužio!");
        }
    }

    private void revertCustomersObligations(Customer customer, Packaging packaging) {
        customer.setPackagingSmall(customer.getPackagingSmall() + packaging.getWaterSmallReturned());
        customer.setPackagingLarge(customer.getPackagingLarge() + packaging.getWaterLargeReturned());
    }

}
