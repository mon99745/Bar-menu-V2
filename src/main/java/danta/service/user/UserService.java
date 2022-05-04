package danta.service.user;

import danta.config.auth.PrincipalDetail;
import danta.domain.user.User;
import danta.domain.user.UserRepository;
import danta.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor //생성자 주입을 받기 위해
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CartService cartService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입 로직
     */
    @Transactional
    public Long save(User user) {
        String hashPw = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashPw);

        // 회원가입과 동시에 장바구니 생성
        Long authId = userRepository.save(user).getAuthId();
        cartService.createCart(authId);

        return userRepository.save(user).getAuthId();
    }

    /**
     * 회원목록 로직
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }
    // orderer 구분을 위함
    public User findUser(Long authId) {
        User user = validateExistMember(userRepository.findById(authId));

        return user;
    }


    /**
     * 회원수정 로직
     */
    @Transactional
    // UserService 클래스에서도
    // @AuthenticationPrincipal PrincipalDetail principalDetail를 파라미터로
    // 받아서 update된 유저 정보를 principalDetail에 집어넣는다.
    public Long update(User user,
                       @AuthenticationPrincipal PrincipalDetail principalDetail) {
        User userEntity = userRepository.findById(user.getAuthId()).orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다. id=" + user.getAuthId()));
        userEntity.update(bCryptPasswordEncoder.encode(user.getPassword()), user.getNickname());
        principalDetail.setUser(userEntity); //추가
        return userEntity.getAuthId();
    }

    /**
     * 회원삭제 로직
     */
    @Transactional
    public void deleteById(Long authId) {
        userRepository.deleteById(authId);
    }

    /**
     * 회원 예외검증
     */
    private User validateExistMember(Optional<User> memberEntity) {
        if(!memberEntity.isPresent())
            throw new IllegalStateException("존재하지 않는 유저입니다.");
        return memberEntity.get();
    }

}