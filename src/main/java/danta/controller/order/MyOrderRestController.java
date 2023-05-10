package danta.controller.order;


import danta.converter.AuthenticationConverter;
import danta.domain.guest.Guest;
import danta.domain.guest.GuestRepository;
import danta.domain.user.User;
import danta.model.dto.order.MyOrderSummaryDto;
import danta.service.guest.GuestService;
import danta.service.order.MyOrderService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

@Slf4j
@Api(tags = MyOrderRestController.TAG,description = "자신의 주문을 관리하는 Rest API")
@ApiIgnore
@RestController
@RequiredArgsConstructor
@RequestMapping(MyOrderRestController.PATH)
public class MyOrderRestController {
    public static final String PATH = "/auth/api/v1";
    public static final String TAG = "My Order Rest API";
    private final MyOrderService myOrderService;
    private final GuestService guestService;
    private final AuthenticationConverter authenticationConverter;
    private final GuestRepository guestRepository;

    @GetMapping("/order")
    public MyOrderSummaryDto getMoreOrderList(Authentication authentication,
                                              Pageable pageable, HttpSession session) {
        MyOrderSummaryDto myOrderSummaryDto;
        if(authentication == null) {
            Guest guest = guestService.findGuestById(Long.valueOf(session.getId().hashCode()));
            myOrderSummaryDto = myOrderService.getMyOrderSummary(guest.getId(), pageable);
        } else{
            User user = authenticationConverter.getUserFromAuthentication(authentication);
            myOrderSummaryDto = myOrderService.getMyOrderSummary(user.getId(), pageable);

        }
        return myOrderSummaryDto;
    }
}

