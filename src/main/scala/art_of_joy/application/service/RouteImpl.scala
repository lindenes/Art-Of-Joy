package art_of_joy.application.service

import art_of_joy.application.http._

object RouteImpl {

  val categoryImpl = List(
    CategoryEndpoint.getCategoryEndpoint implement AppHandler.getCategory,
    CategoryEndpoint.categoryAddEndpoint implement AppHandler.addCategory,
    CategoryEndpoint.getBrandEndpoint    implement AppHandler.getBrand,
    CategoryEndpoint.brandAddEndpoint    implement AppHandler.addBrand
  )

  val productImpl = List(
    ProductEndpoint.exelEndpoint            implement AppHandler.parseExel,
    ProductEndpoint.productGetEndpoint      implement AppHandler.getProduct,
    ProductEndpoint.productAddEndpoint      implement AppHandler.addProduct,
    ProductEndpoint.productPhotoAddEndpoint implement AppHandler.addProductPhoto,
    ProductEndpoint.getCartEndpoint         implement AppHandler.getPersonCart,
    ProductEndpoint.addCartEndpoint         implement AppHandler.addToCart,
    ProductEndpoint.deleteCartEndpoint      implement AppHandler.deleteFromCart
  )

  val personImpl = List(
    PersonEndpoint.personEndpoint       implement AppHandler.getUserList,
    PersonEndpoint.personInfoEndpoint   implement AppHandler.setPersonInfo,
    PersonEndpoint.registrationEndpoint implement AppHandler.registration,
    PersonEndpoint.authEndpoint         implement AppHandler.authorization,
    PersonEndpoint.acceptCodeEndpoint   implement AppHandler.checkAcceptCode,
    PersonEndpoint.passwordEndpoint     implement AppHandler.setPassword
  )

}
