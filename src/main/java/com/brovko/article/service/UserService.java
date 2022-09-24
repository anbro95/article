package com.brovko.article.service;


import com.brovko.article.model.Article;
import com.brovko.article.model.Job;
import com.brovko.article.model.Role;
import com.brovko.article.model.User;
import com.brovko.article.repository.ArticleRepository;
import com.brovko.article.repository.JobRepository;
import com.brovko.article.repository.RoleRepository;
import com.brovko.article.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final ArticleRepository articleRepository;
    private final JobRepository jobRepository;

    public String addArticleToUser(Long userId, Long articleId) {
        log.info("Trying to add article {} to user {}", articleId, userId);
        User user = userRepository.findById(userId).orElse(null);
        Article article = articleRepository.findById(articleId).orElse(null);

        if(user == null) return "User " + userId + " not found";
        if(article == null) return "Article " + articleId + " not found";

        user.getArticles().add(article);
        userRepository.save(user);

        return "Article added to user successfully!";
    }

    public String addJobToUser(Long userId, Long jobId) {
        log.info("Trying to add Job {} to user {}", jobId, userId);
        User user = userRepository.findById(userId).orElse(null);
        Job job = jobRepository.findById(jobId).orElse(null);

        if(user == null) return "User " + userId + " not found";
        if(job == null) return "Job " + jobId + " not found";

        user.getJobs().add(job);
        userRepository.save(user);

        return "Job added to user successfully!";
    }

    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    public List<Role> roles() {
        log.info("Fetching all roles");
        return roleRepository.findAll();
    }

    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        User user = userRepository.findByUserName(username).orElse(null);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
        userRepository.save(user);
    }


    public User saveUser(User user) {
        log.info("Saving user with id {}", user.getUser_id());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        log.info("Looking for user with id {}", id);
        return userRepository.findById(id).orElse(null);

    }

    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }

    public User getUserByUserName(String userName) {
        log.info("Getting user by Username {}", userName);
        return userRepository.findByUserName(userName).orElse(null);
    }

    public String deleteUserById(Long id) {
        log.info("Deleting user by ID {}", id);
        try{
            userRepository.deleteById(id);
            return "Successfully deleted!!!";
        } catch (Exception e) {
            return "User not found with id " + id;
        }

    }

    public User updateUser(User user) {
        log.info("Updating user info {}", user.getUser_id());
        Long id = user.getUser_id();
        User updatedUser = userRepository.findById(id).orElse(null);
        if(updatedUser == null) {
            return null;
        }

        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        updatedUser.setCity(user.getCity());
        updatedUser.setAge(user.getAge());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setCountry(user.getCountry());
        updatedUser.setPassword(user.getPassword());
        updatedUser.setBirthDate(user.getBirthDate());
        updatedUser.setCreatedAt(user.getCreatedAt());

        updatedUser.setPhone(user.getPhone());
//        updatedUser.setArticles(user.getArticles());
//        updatedUser.setSocialMediaLinks(user.getSocialMediaLinks());
        updatedUser.setCreditCardNumber(user.getCreditCardNumber());
//        updatedUser.setJob(user.getJob());

        return userRepository.save(updatedUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).orElse(null);
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);

        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {authorities.add(new SimpleGrantedAuthority(role.getName()));});
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
    }
}
