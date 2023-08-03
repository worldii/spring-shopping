package shopping.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import shopping.acceptance.helper.AuthHelper;
import shopping.acceptance.helper.CartProductHelper;
import shopping.dto.request.CartProductRequest;
import shopping.dto.response.CartResponse;

@DisplayName("장바구니 상품 관련 기능 인수 테스트")
class CartProductAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("장바구니 상품 관련 기능은 인증되지 않으면 사용할 수 없다.")
    void checkAuthentication() {
        /* given */
        final String jwt = "abcd";
        final CartProductRequest request = new CartProductRequest(3L);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
            .body(request)
            .when().post("/api/cart")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("장바구니 상품을 추가한다.")
    void createCartProduct() {
        /* given */
        final String jwt = AuthHelper.login("woowacamp@naver.com", "woowacamp");
        final CartProductRequest request = new CartProductRequest(3L);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
            .body(request)
            .when().post("/api/cart")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("상품이 존재하지 않는 경우 \"존재하지 않는 상품입니다.\"로 응답한다.")
    void createCartProductBadRequestWithDoesNotExistProduct() {
        /* given */
        final String jwt = AuthHelper.login("woowacamp@naver.com", "woowacamp");
        final CartProductRequest request = new CartProductRequest(123L);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
            .body(request)
            .when().post("/api/cart")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(
            "존재하지 않는 상품입니다. 입력값: 123");
    }

    @Test
    @DisplayName("장바구니 상품이 이미 존재하는 경우 \"이미 장바구니에 담긴 상품입니다.\"로 응답한다.")
    void createCartProductBadRequestWithExistCartProduct() {
        /* given */
        final String jwt = AuthHelper.login("woowacamp@naver.com", "woowacamp");
        final CartProductRequest request = new CartProductRequest(3L);
        CartProductHelper.createCartProduct(jwt, request);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
            .body(request)
            .when().post("/api/cart")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
            .isEqualTo("이미 장바구니에 담긴 상품입니다. 입력값: 3");
    }


    @Test
    @DisplayName("장바구니 상품을 모두 조회한다.")
    void getAllCartProducts() {
        /* given */
        final String jwt = AuthHelper.login("woowacamp@naver.com", "woowacamp");
        CartProductHelper.createCartProduct(jwt, new CartProductRequest(3L));

        /* when */
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
            .when().get("/api/cart")
            .then().log().all()
            .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(new TypeRef<List<CartResponse>>() {
        })).hasSize(3);
    }
}
