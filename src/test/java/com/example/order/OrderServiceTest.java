package com.example.order;

import com.example.order.domain.*;
import com.example.order.repository.*;
import com.example.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    private Member normalMember;
    private Member vipMember;
    private Product product;

    @BeforeEach
    void setUp() {
        normalMember = memberRepository.save(Member.builder()
                .name("일반회원")
                .grade(MemberGrade.NORMAL)
                .build());

        vipMember = memberRepository.save(Member.builder()
                .name("VIP회원")
                .grade(MemberGrade.VIP)
                .build());

        product = productRepository.save(Product.builder()
                .name("테스트상품")
                .price(10000)
                .stock(100)
                .build());
    }

    @Test
    @DisplayName("[1] [필수] 일반회원_쿠폰없음_정가결제")
    void 일반회원_쿠폰없음_정가결제() {
        // given
        int quantity = 2;

        // when
        Order order = orderService.createOrder(normalMember.getId(), product.getId(), quantity, null);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(20000); // 10000 * 2
        assertThat(order.getMember().getGrade()).isEqualTo(MemberGrade.NORMAL);
    }

    @Test
    @DisplayName("[2] [필수] VIP회원_10퍼센트_할인적용")
    void VIP회원_10퍼센트_할인적용() {
        // given
        int quantity = 2;

        // when
        Order order = orderService.createOrder(vipMember.getId(), product.getId(), quantity, null);

        // then
        int expectedPrice = (int) (20000 * 0.9); // 10% 할인
        assertThat(order.getTotalPrice()).isEqualTo(expectedPrice);
        assertThat(order.getMember().getGrade()).isEqualTo(MemberGrade.VIP);
    }

    @Test
    @DisplayName("[3] [필수] 최소주문금액_미달시_예외발생")
    void 최소주문금액_미달시_예외발생() {
        // given
        Product cheapProduct = productRepository.save(Product.builder()
                .name("저가상품")
                .price(1000)
                .stock(10)
                .build());
        int quantity = 1; // 1000원 - 최소주문금액(5000원) 미달

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(normalMember.getId(), cheapProduct.getId(), quantity, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최소주문금액");
    }

    @Test
    @DisplayName("[4] [필수] 쿠폰_중복적용_불가_오류검증")
    void 쿠폰_중복적용_불가_오류검증() {
        // given
        Coupon coupon = couponRepository.save(Coupon.builder()
                .code("DISCOUNT10")
                .discountRate(10)
                .member(normalMember)
                .used(false)
                .build());

        // 첫 번째 주문 - 쿠폰 사용
        orderService.createOrder(normalMember.getId(), product.getId(), 3, coupon.getId());

        // when & then - 동일 쿠폰으로 두 번째 주문 시도
        assertThatThrownBy(() -> orderService.createOrder(normalMember.getId(), product.getId(), 2, coupon.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용된 쿠폰");
    }

    @Test
    @DisplayName("[5] [필수] 주문금액_3만원_미만이면_배송비_3000원_추가")
    void 주문금액이_3만원_미만이면_배송비_3000원_추가() {
        // given
        int quantity = 2; // 20000원 - 3만원 미만

        // when
        Order order = orderService.createOrder(normalMember.getId(), product.getId(), quantity, null);

        // then
        assertThat(order.getDeliveryFee()).isEqualTo(3000);
        assertThat(order.getTotalPrice()).isEqualTo(20000 + 3000); // 상품가 + 배송비
    }

    /**
     * [6] [선택] 동시성 테스트
     * 도전하려면 아래 주석을 해제하고 테스트를 통과시키세요.
     *
     * 힌트: 동시성 제어를 하지 않으면 재고가 정확히 차감되지 않습니다.
     * 비관적 락(Pessimistic Lock) 또는 낙관적 락(Optimistic Lock)을 고려해보세요.
     */
//    @Test
//    @DisplayName("[6] [선택] 동시에_100명이_요청해도_재고는_정확히_줄어야한다")
//    void 동시에_100명이_요청해도_재고는_정확히_줄어야한다() throws InterruptedException {
//        // given
//        Product concurrentProduct = productRepository.save(Product.builder()
//                .name("동시성테스트상품")
//                .price(10000)
//                .stock(100)
//                .build());
//
//        int threadCount = 100;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch readyLatch = new CountDownLatch(threadCount);  // 모든 스레드 준비 대기
//        CountDownLatch startLatch = new CountDownLatch(1);            // 동시 시작 신호
//        CountDownLatch endLatch = new CountDownLatch(threadCount);    // 모든 스레드 완료 대기
//
//        // 테스트용 회원 미리 생성
//        List<Member> members = new ArrayList<>();
//        for (int i = 0; i < threadCount; i++) {
//            members.add(memberRepository.save(Member.builder()
//                    .name("테스트회원" + i)
//                    .grade(MemberGrade.NORMAL)
//                    .build()));
//        }
//
//        // when
//        for (int i = 0; i < threadCount; i++) {
//            final int index = i;
//            executorService.submit(() -> {
//                try {
//                    readyLatch.countDown();  // 준비 완료 신호
//                    startLatch.await();       // 시작 신호 대기 (모든 스레드가 동시에 시작)
//                    orderService.createOrder(members.get(index).getId(), concurrentProduct.getId(), 1, null);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                } finally {
//                    endLatch.countDown();
//                }
//            });
//        }
//
//        readyLatch.await();   // 모든 스레드가 준비될 때까지 대기
//        startLatch.countDown(); // 동시 시작!
//        endLatch.await();     // 모든 스레드 완료 대기
//        executorService.shutdown();
//
//        // then
//        Product updatedProduct = productRepository.findById(concurrentProduct.getId()).orElseThrow();
//        assertThat(updatedProduct.getStock()).isEqualTo(0);
//    }
}
