package com.exam.repository;

//step 4 a)

import com.exam.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

//one interface can extends the another  interface.
public interface UserRepository  extends JpaRepository<User,Long>
{
   // only declared , this methed is implemented by spring itself .
   //custom finder method. - this will be used by spring security class UserDetailsImpl  to check user with username.
    public  User findUserByUsername(String username);


}
