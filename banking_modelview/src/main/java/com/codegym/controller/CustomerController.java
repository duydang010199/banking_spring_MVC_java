package com.codegym.controller;

import com.codegym.model.Customer;
import com.codegym.model.Deposit;
import com.codegym.model.Withdraw;
import com.codegym.service.customer.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping({"/customers", ""})
public class CustomerController {
    @Autowired
    private ICustomerService customerService;

    @GetMapping
    public ModelAndView showListPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/list");
        List<Customer> customers = customerService.findAllByDeletedIsFalse();
        modelAndView.addObject("customers", customers);

        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView showCreatePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/create");
        modelAndView.addObject("customer", new Customer());
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView doCreateCustomer(@Validated @ModelAttribute Customer customer,
                                 BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/create");

        if (bindingResult.hasFieldErrors()) {
            modelAndView.addObject("error", true);
            return modelAndView;
        }

        try {
            customer.setId(0L);
            customer.setBalance(new BigDecimal(0L));
            customerService.save(customer);

            modelAndView.addObject("customer", new Customer());
            modelAndView.addObject("success", true);
        } catch (Exception e) {
            modelAndView.addObject("error", true);
        }
        return modelAndView;
    }

    @GetMapping("/edit/{customerId}")
    public ModelAndView showEditPage(@PathVariable long customerId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/edit");
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            modelAndView.addObject("error", true);
        }
        Customer customer = customerOptional.get();
        modelAndView.addObject("customer", customer);

        return modelAndView;
    }

    @PutMapping("/edit/{customerId}")
    public ModelAndView doUpdateCustomer(@PathVariable long customerId, @Validated @ModelAttribute Customer customer, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/edit");

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            modelAndView.addObject("errorAction", "ID Kh??ch h??ng kh??ng h???p l???!");
            return modelAndView;
        }

        if (bindingResult.hasFieldErrors()) {
            modelAndView.addObject("error", true);
            return modelAndView;
        }
        try {
            customer.setId(customerId);
            customerService.save(customer);
            modelAndView.addObject("success", true);
        } catch (Exception e) {
            modelAndView.addObject("errorAction", "Thao t??c kh??ng th??nh c??ng!");
        }
        modelAndView.addObject("customer", customer);
        return modelAndView;
    }

    @GetMapping("/delete/{customerId}")
    public ModelAndView deleteCustomerById(@PathVariable long customerId, RedirectAttributesModelMap redirectAttributesModelMap) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:" + "/customers");
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            redirectAttributesModelMap.addFlashAttribute("error", "Thao t??c kh??ng th??nh c??ng!");
        }
        Customer customer = customerOptional.get();
        customer.setDeleted(true);
        customerService.save(customer);

        redirectAttributesModelMap.addFlashAttribute("success", "X??a th??nh c??ng!");

        return modelAndView;
    }

    @GetMapping("/deposit/{customerId}")
    public ModelAndView showDepositPage(@PathVariable long customerId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/deposit");

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            modelAndView.addObject("error", "Id kh??ch h??ng kh??ng h???p l???!");
        } else {
            Deposit deposit = new Deposit();
            modelAndView.addObject("deposit", deposit);
            modelAndView.addObject("customer", customerOptional.get());
        }
        return modelAndView;
    }

    @PostMapping("/deposit/{cid}")
    public ModelAndView depositMoney(@Validated @ModelAttribute Deposit deposit,
                                BindingResult bindingResult,
                                @PathVariable Long cid) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/deposit");

        Optional<Customer> customerOptional = customerService.findById(cid);

        if (!customerOptional.isPresent()) {
            modelAndView.addObject("error", "ID kh??ch h??ng kh??ng h???p l???!");
            return modelAndView;
        }

        Customer customer = customerOptional.get();

        if (bindingResult.hasFieldErrors()) {
            modelAndView.addObject("customer", customer);
            modelAndView.addObject("error", true);
            return modelAndView;
        }
        try {
            customerService.deposit(deposit, customer);

            modelAndView.addObject("customer", customer);
            modelAndView.addObject("deposit", new Deposit());
            modelAndView.addObject("success", "G???i ti???n th??nh c??ng!");
        } catch (Exception e) {
            modelAndView.addObject("customer", customer);
            modelAndView.addObject("deposit", new Deposit());
            modelAndView.addObject("errorAction", "Thao t??c kh??ng th??nh c??ng!");
        }

        return modelAndView;
    }

    @GetMapping("/withdraw/{customerId}")
    public ModelAndView showWithdrawPage(@PathVariable long customerId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/withdraw");

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            modelAndView.addObject("error", "Id kh??ch h??ng kh??ng h???p l???!");
        } else {
            Withdraw withdraw = new Withdraw();
            modelAndView.addObject("withdraw", withdraw);
            modelAndView.addObject("customer", customerOptional.get());
        }
        return modelAndView;
    }

    @PostMapping("/withdraw/{cid}")
    public ModelAndView withdrawMoney(@Validated @ModelAttribute Withdraw withdraw,
                                 BindingResult bindingResult,
                                 @PathVariable Long cid) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customer/withdraw");

        Optional<Customer> customerOptional = customerService.findById(cid);

        if (!customerOptional.isPresent()) {
            modelAndView.addObject("error", "Id kh??ch h??ng kh??ng h???p l???!");
            return modelAndView;
        }

        Customer customer = customerOptional.get();

        if (bindingResult.hasFieldErrors()) {
            modelAndView.addObject("customer", customer);
            modelAndView.addObject("error", true);
            return modelAndView;
        }
        try {
            if (customerService.withdraw(withdraw, customer)) {
                modelAndView.addObject("customer", customer);
                modelAndView.addObject("withdraw", new Withdraw());
                modelAndView.addObject("success", "R??t ti???n th??nh c??ng");
            } else {
                modelAndView.addObject("customer", customer);
                modelAndView.addObject("withdraw", new Withdraw());
                modelAndView.addObject("errorAction", "Thao t??c kh??ng th??nh c??ng!");
            }

        } catch (Exception e) {
            modelAndView.addObject("customer", customer);
            modelAndView.addObject("withdraw", new Withdraw());
            modelAndView.addObject("errorAction", "Thao t??c kh??ng th??nh c??ng!");
        }

        return modelAndView;
    }
}
