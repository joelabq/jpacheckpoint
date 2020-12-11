package com.example.JPACheckpoint;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("")
    @JsonView(Views.UserViewNoPass.class)
    public User newUser(@RequestBody User user)  {      return this.repository.save(user);   }

    @GetMapping("")
    @JsonView(Views.UserViewNoPass.class)
    public Iterable<User> getUserList(){   return this.repository.findAll();   }

    @GetMapping("/{id}")
    @JsonView(Views.UserViewNoPass.class)
    public Optional<User> getSingleUser(@PathVariable Long id){
        return this.repository.findById(id);
    }


    @PatchMapping("/{id}")
    @JsonView(Views.UserViewNoPass.class)
    public User updateUser(@PathVariable Long id, @RequestBody User user){
         User updatedUser = user;
        Optional<User> updateUserRecord = this.repository.findById(id);

        updateUserRecord.ifPresent(field -> {
            field.setEmail(user.getEmail());
            field.setPassword(user.getPassword());
        });


        if (updateUserRecord.isPresent()){
            updatedUser = updateUserRecord.get();
        }

        return this.repository.save(updatedUser);
    }
    @DeleteMapping("/{id}")
    public HashMap<String, Object> deleteUser(@PathVariable Long id){
        this.repository.deleteById(id);
        return new HashMap<String, Object>(){
            {put("count",repository.count());}
        };
    }
    @PostMapping("/authenticate")

    public AuthenticatedUser authenticateUser(@RequestBody User user){
        User getUser = this.repository.findByEmail(user.getEmail());
        AuthenticatedUser authUser;
        boolean authenticated;
        Optional<User> userAuthLookup = this.repository.findById(getUser.getId());

        if (userAuthLookup.isPresent() && userAuthLookup.get().getPassword().equals(user.getPassword())){
            authUser = new AuthenticatedUser(true,getUser);
        }
        else authUser = new AuthenticatedUser();

        return authUser;
    }

}
