package com.joro.driveguard.controller;

import com.joro.driveguard.Utilities;
import com.joro.driveguard.model.User;
import com.joro.driveguard.model.dto.UserCredentialsDTO;
import com.joro.driveguard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class UserController
{
    @Autowired
    private UserService userService;

    @PostMapping("/androidLogin")
    @ResponseBody
    public String androidLogin(@RequestBody UserCredentialsDTO dto)
    {
        User user = userService.isValidUserCredentialsDTO(dto);
        if (user == null)
        {
            return null;
        }
        return user.getAPIKey();
    }

    @GetMapping(value = {"/", "/login"})
    public ModelAndView login()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping(value = "/registration")
    public ModelAndView registration()
    {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @PostMapping(value = "/registration")
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult)
    {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByPhoneNumber(user.getPhoneNumber());
        if (userExists != null)
        {
            bindingResult.rejectValue("phoneNumber", "error.user", "Вече съществува регистриран потребител с този номер");
        }
        if (bindingResult.hasErrors())
        {
            modelAndView.setViewName("registration");
        } else
        {
            final String APIKey = Utilities.generateAPIKey();
            if (APIKey == null)
            {
                bindingResult.rejectValue("phoneNumber", "error.user", "Настъпи грешка. Моля опитайте отново.");
                modelAndView.setViewName("registration");
                return modelAndView;
            }
            else
            {
                user.setAPIKey(APIKey);
                userService.saveUser(user);
                modelAndView.addObject("successMessage", "Успешна регистрация");
                modelAndView.addObject("user", new User());
                modelAndView.setViewName("registration");
            }
        }
        return modelAndView;
    }

    @GetMapping(value = "/employee/employee")
    public ModelAndView employee()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("employee/employee");
        return modelAndView;
    }
}
