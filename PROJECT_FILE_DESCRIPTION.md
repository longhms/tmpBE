# Mo ta toan bo project backend

## 1. Tong quan he thong

Day la mot project backend Java dung `Spring Boot 2.7.8`, build bang `Maven`, dung:

- `Spring Web` de tao API REST.
- `Spring Security` de xac thuc va phan quyen.
- `JWT` de giu phien dang nhap theo token.
- `Spring Data JPA` de truy cap du lieu.
- `MySQL` lam database.
- `Flyway` de tao schema va seed du lieu ban dau.
- `HikariCP` lam connection pool.
- `MapStruct` da duoc them dependency, nhung hien tai chua duoc su dung trong flow nghiep vu chinh.

Project hien tai tap trung vao 1 flow chinh:

1. User goi `POST /login` voi `username` va `password`.
2. Spring Security dung `AuthenticationManager` + `UserDetailsServiceImpl` de tim user trong bang `employees`.
3. Neu hop le, he thong sinh JWT bang `JwtTokenProvider`.
4. Cac request sau do gui `Authorization: Bearer <token>`.
5. `JwtTokenFilter` doc token, xac minh token, nap lai user vao `SecurityContext`.
6. Cac endpoint private moi duoc phep truy cap.

## 2. Cau truc logic hien tai

### 2.1 Entry point

- `MainApplication` la diem khoi dong cua Spring Boot.
- Mac dinh project chay profile `dev` neu khong truyen profile.
- App log ra URL local/external va profile dang dung luc startup.

### 2.2 Database

- Bang du lieu chinh hien tai la `employees`.
- Script `Flyway` tao bang va insert san 1 tai khoan `admin`.
- `Employee` la entity map toi bang `employees`.
- `EmployeeRepository` la noi truy van bang employee.

### 2.3 Security va JWT

- `SecurityConfiguration` cau hinh Spring Security theo kieu stateless.
- `AuthController` xu ly dang nhap.
- `JwtTokenProvider` sinh va verify token.
- `JwtTokenFilter` doc token tu header `Authorization`.
- `UserDetailsServiceImpl` nap user tu DB.
- Role hien tai dang fix cung la `ROLE_USER` cho moi employee tim thay.

### 2.4 API hien co

- `GET /`:
  Tra chuoi welcome.
- `POST /login`:
  Dang nhap va tra token neu dung thong tin.
- `GET/POST /test-auth`:
  API test token, can da duoc xac thuc.

## 3. Danh sach file va chuc nang tung file

### 3.1 Thu muc goc

`/.gitignore`
- Khai bao cac file/thu muc Git se bo qua. Thuong dung de bo qua file build, file IDE, log, file tam.

`/mvnw`
- Maven Wrapper script cho macOS/Linux. Cho phep chay Maven ma khong can cai san Maven toan cuc.

`/mvnw.cmd`
- Maven Wrapper script cho Windows. Day la file thuong dung nhat neu chay tren Windows Command Prompt/PowerShell.

`/pom.xml`
- File cau hinh Maven trung tam.
- Khai bao:
  - Ten artifact `user-manage`.
  - Java 17.
  - Dependency Spring Web, Security, JPA, Flyway, MySQL, HikariCP, JWT, Lombok, MapStruct.
  - 2 profile build/runtime: `dev` va `prod`.
  - Plugin `spring-boot-maven-plugin`.

`/README.md`
- File huong dan khoi dong project, login API, su dung JWT va mo ta so bo cau truc source.

`/PROJECT_FILE_DESCRIPTION.md`
- File tai lieu nay. Muc dich la giai thich project va vai tro cua tung file hien co.

### 3.2 Thu muc `.idea`

Day la metadata cua IntelliJ IDEA. Nhung file nay phuc vu IDE, khong tham gia logic runtime cua backend.

`/.idea/.gitignore`
- Cau hinh bo qua mot so file local cua IntelliJ trong thu muc `.idea`, dac biet la `workspace.xml`.

`/.idea/compiler.xml`
- Cau hinh compiler cua IntelliJ.
- Bat annotation processing cho project Maven, can thiet cho cac thu vien nhu Lombok/MapStruct.

`/.idea/encodings.xml`
- Khai bao encoding cho source, o day la UTF-8 cho `src/main/java`.

`/.idea/jarRepositories.xml`
- Danh sach Maven repositories ma IntelliJ co the dung de resolve dependency.

`/.idea/misc.xml`
- Metadata tong quat cua project tren IntelliJ:
  - Project dung JDK 17.
  - Maven project goc la `pom.xml`.

`/.idea/workspace.xml`
- File workspace cuc bo cua IntelliJ cho may dang mo project.
- Thuong chua thong tin tam nhu cua so dang mo, vi tri caret, task local, run config local.
- Khong nen xem la mot phan logic cua he thong.

### 3.3 Thu muc `.mvn`

`/.mvn/wrapper/maven-wrapper.jar`
- File nhi phan cua Maven Wrapper.
- Dung de bootstrap Maven dung version duoc khai bao trong `maven-wrapper.properties`.

`/.mvn/wrapper/maven-wrapper.properties`
- Cau hinh Maven Wrapper.
- Chi ra URL tai Maven `3.8.6` va wrapper JAR version `3.1.0`.

### 3.4 Java source trong `src/main/java`

#### Package goc `com.luvina.la`

`/src/main/java/com/luvina/la/MainApplication.java`
- Lop chay chinh cua Spring Boot.
- `@SpringBootApplication` kich hoat component scan, auto configuration va bean registration.
- Kiem tra xung dot profile `dev` va `prod`.
- Set default profile bang `DefaultProfileUtil`.
- Log thong tin startup sau khi app chay.

#### Package `config`

`/src/main/java/com/luvina/la/config/Constants.java`
- Chua cac hang so dung chung cho project:
  - Ten profile `dev`, `prod`.
  - Bat/tat CORS mo rong.
  - JWT secret va thoi gian het han.
  - Danh sach endpoint public.
  - Danh sach endpoint can role.
  - Danh sach field employee duoc dua vao token.

`/src/main/java/com/luvina/la/config/DefaultProfileUtil.java`
- Helper set profile mac dinh cho Spring Boot.
- Neu app khong duoc chay voi profile nao, no se tu dong dung `dev`.

`/src/main/java/com/luvina/la/config/PersistenceConfiguration.java`
- Cau hinh persistence layer.
- Mo entity scan trong `com.luvina.la.entity`.
- Mo JPA repository scan trong `com.luvina.la.repository`.
- Dung property `spring.datasource` de tao `HikariDataSource`.

`/src/main/java/com/luvina/la/config/SecurityConfiguration.java`
- Cau hinh Spring Security trung tam.
- Tao bean:
  - `JwtTokenFilter`
  - `AuthenticationManager`
  - `PasswordEncoder` dung BCrypt
  - `CorsFilter`
- Thiet lap:
  - Tat CSRF.
  - Session stateless.
  - Endpoint public lay tu `Constants.ENDPOINTS_PUBLIC`.
  - Endpoint `/user/**` can `ROLE_USER`.
  - Moi endpoint khac mac dinh can authenticated.
  - Gan `AuthEntryPoint` cho request khong du quyen.

`/src/main/java/com/luvina/la/config/WebConfiguration.java`
- Cau hinh web co ban.
- Khi app startup, no log profile dang su dung va xac nhan web layer da config xong.

#### Package `config.jwt`

`/src/main/java/com/luvina/la/config/jwt/AuthEntryPoint.java`
- Xu ly truong hop request khong duoc xac thuc.
- Khi xay ra loi auth, tra HTTP `401 Unauthorized`.

`/src/main/java/com/luvina/la/config/jwt/AuthUserDetails.java`
- Adapter bien `Employee` thanh `UserDetails` cua Spring Security.
- Tra password/username cho Security framework.
- Chua collection `authorities` cua user.

`/src/main/java/com/luvina/la/config/jwt/JwtTokenFilter.java`
- Filter chay moi request.
- Doc header `Authorization`.
- Neu token hop le:
  - Lay username tu token.
  - Load user tu DB.
  - Tao `UsernamePasswordAuthenticationToken`.
  - Dua thong tin vao `SecurityContextHolder`.

`/src/main/java/com/luvina/la/config/jwt/JwtTokenProvider.java`
- Sinh JWT va validate JWT.
- `generateToken(...)` tao token voi:
  - issuer
  - issue time
  - expire time
  - subject = `employeeLoginId`
  - claim `employee` gom mot so field employee
- `validateToken(...)` verify chu ky HMAC512 va kiem tra han su dung.
- `getUsernameFromJWT(...)` lay `subject` tu token.

`/src/main/java/com/luvina/la/config/jwt/UserDetailsServiceImpl.java`
- Trien khai `UserDetailsService` cua Spring Security.
- Tim `Employee` theo `employeeLoginId`.
- Neu tim thay thi gan role mac dinh `ROLE_USER`.
- Neu khong tim thay thi nem `UsernameNotFoundException`.

#### Package `controller`

`/src/main/java/com/luvina/la/controller/AuthController.java`
- Controller auth.
- `POST /login`:
  - Nhan `LoginRequest`.
  - Goi `AuthenticationManager.authenticate(...)`.
  - Neu thanh cong thi sinh JWT va tra `LoginResponse`.
  - Neu sai tai khoan/mat khau thi tra `errors.code = 100`.
  - Neu loi khac thi tra `errors.code = 000`.
- `/test-auth`:
  - API test xem token co hop le va da qua filter security chua.

`/src/main/java/com/luvina/la/controller/HomeController.java`
- Controller cuc don gian cho endpoint `/`.
- Tra ve chuoi welcome, xem nhu endpoint public de test app song.

#### Package `dto`

`/src/main/java/com/luvina/la/dto/EmployeeDTO.java`
- DTO de truyen thong tin employee ra ngoai hoac giua cac layer.
- Khong chua password.
- Hien tai chua thay duoc su dung trong controller/service.

#### Package `entity`

`/src/main/java/com/luvina/la/entity/Employee.java`
- Entity JPA map toi bang `employees`.
- Cac cot dang map:
  - `employee_id`
  - `employee_name`
  - `employee_email`
  - `employee_login_id`
  - `employee_login_password`
- Chu y:
  SQL migration con co cot `department_id`, nhung entity hien tai khong map cot nay.

#### Package `mapper`

`/src/main/java/com/luvina/la/mapper/EmployeeMapper.java`
- Interface MapStruct de convert giua object.
- Muc dich du kien la convert `EmployeeDTO` <-> `Employee`.
- Hien tai co 1 diem bat thuong:
  - `toDto` dang khai bao tra `Employee` thay vi `EmployeeDTO`.
  - Mapper nay co ve chua duoc hoan thien hoac chua duoc su dung thuc te.

#### Package `payload`

`/src/main/java/com/luvina/la/payload/LoginRequest.java`
- Payload dau vao cho API login.
- Chua 2 truong: `username`, `password`.

`/src/main/java/com/luvina/la/payload/LoginResponse.java`
- Payload dau ra cho API login.
- Chua:
  - `accessToken`
  - `tokenType`
  - `errors`
- Co 2 constructor:
  - Constructor tra token thanh cong.
  - Constructor tra map loi.

#### Package `repository`

`/src/main/java/com/luvina/la/repository/EmployeeRepository.java`
- Spring Data repository cho `Employee`.
- Ke thua `CrudRepository<Employee, Long>`.
- Co them 2 method query:
  - `findByEmployeeLoginId(...)`
  - `findByEmployeeId(...)`

### 3.5 Resources trong `src/main/resources`

`/src/main/resources/banner.txt`
- Banner ASCII duoc in ra luc Spring Boot startup.

`/src/main/resources/logback-spring.xml`
- Cau hinh Logback.
- Include cau hinh base cua Spring Boot.
- Giam log level cua nhieu package framework de log gon hon.
- Co shutdown hook va bridge cho JUL.

#### Thu muc `config`

`/src/main/resources/config/application.yaml`
- Cau hinh dung chung:
  - Ten app `user-manage`
  - Port `8085`
  - Flyway enable
  - JPA `ddl-auto: none`
  - Hibernate naming strategy
  - `open-in-view: false`
  - Dat `allow-circular-references: true`
- Chu y:
  file nay dang `exclude` `DataSourceAutoConfiguration`, trong khi project tu tao datasource bang `PersistenceConfiguration`.

`/src/main/resources/config/application-dev.yaml`
- Cau hinh moi truong `dev`.
- Chua thong tin ket noi MySQL local o port `3306`.
- Bat debug/trace log cho package app, SQL, binder va Hikari.
- Expose endpoint metrics.

`/src/main/resources/config/application-prod.yaml`
- Cau hinh moi truong `prod`.
- Ket noi MySQL local o port `3307`.
- Log level it chi tiet hon.
- Cung expose metrics.

#### Thu muc `db/migration`

`/src/main/resources/db/migration/V1__init_schema.sql`
- Script Flyway phien ban 1.
- Tao bang `employees` neu chua ton tai.
- Insert san 1 user admin:
  - login id: `admin`
  - password da BCrypt hash

### 3.6 Thu muc `target`

Day la output sinh ra sau khi build/chay project. Thu muc nay khong phai source code goc, nhung no dang ton tai trong workspace hien tai.

`/target/user-manage-0.0.1.jar`
- File JAR da dong goi de chay ung dung.

`/target/user-manage-0.0.1.jar.original`
- Ban JAR goc truoc khi Spring Boot repackage.

#### `target/classes`

`/target/classes/banner.txt`
- Ban copy da duoc dua vao classpath cua `src/main/resources/banner.txt`.

`/target/classes/logback-spring.xml`
- Ban copy da duoc dua vao classpath cua `src/main/resources/logback-spring.xml`.

`/target/classes/com/luvina/la/MainApplication.class`
- Bytecode da compile tu `MainApplication.java`.

`/target/classes/com/luvina/la/config/Constants.class`
- Bytecode da compile tu `Constants.java`.

`/target/classes/com/luvina/la/config/DefaultProfileUtil.class`
- Bytecode da compile tu `DefaultProfileUtil.java`.

`/target/classes/com/luvina/la/config/PersistenceConfiguration.class`
- Bytecode da compile tu `PersistenceConfiguration.java`.

`/target/classes/com/luvina/la/config/SecurityConfiguration.class`
- Bytecode da compile tu `SecurityConfiguration.java`.

`/target/classes/com/luvina/la/config/WebConfiguration.class`
- Bytecode da compile tu `WebConfiguration.java`.

`/target/classes/com/luvina/la/config/jwt/AuthEntryPoint.class`
- Bytecode da compile tu `AuthEntryPoint.java`.

`/target/classes/com/luvina/la/config/jwt/AuthUserDetails.class`
- Bytecode da compile tu `AuthUserDetails.java`.

`/target/classes/com/luvina/la/config/jwt/JwtTokenFilter.class`
- Bytecode da compile tu `JwtTokenFilter.java`.

`/target/classes/com/luvina/la/config/jwt/JwtTokenProvider.class`
- Bytecode da compile tu `JwtTokenProvider.java`.

`/target/classes/com/luvina/la/config/jwt/UserDetailsServiceImpl.class`
- Bytecode da compile tu `UserDetailsServiceImpl.java`.

`/target/classes/com/luvina/la/controller/AuthController.class`
- Bytecode da compile tu `AuthController.java`.

`/target/classes/com/luvina/la/controller/HomeController.class`
- Bytecode da compile tu `HomeController.java`.

`/target/classes/com/luvina/la/dto/EmployeeDTO.class`
- Bytecode da compile tu `EmployeeDTO.java`.

`/target/classes/com/luvina/la/entity/Employee.class`
- Bytecode da compile tu `Employee.java`.

`/target/classes/com/luvina/la/mapper/EmployeeMapper.class`
- Bytecode da compile tu `EmployeeMapper.java`.

`/target/classes/com/luvina/la/payload/LoginRequest.class`
- Bytecode da compile tu `LoginRequest.java`.

`/target/classes/com/luvina/la/payload/LoginResponse.class`
- Bytecode da compile tu `LoginResponse.java`.

`/target/classes/com/luvina/la/repository/EmployeeRepository.class`
- Bytecode da compile tu `EmployeeRepository.java`.

`/target/classes/config/application-dev.yaml`
- Ban resource da copy tu `src/main/resources/config/application-dev.yaml`.

`/target/classes/config/application-prod.yaml`
- Ban resource da copy tu `src/main/resources/config/application-prod.yaml`.

`/target/classes/config/application.yaml`
- Ban resource da copy tu `src/main/resources/config/application.yaml`.

`/target/classes/db/migration/V1__init_schema.sql`
- Ban resource da copy tu migration SQL goc.

`/target/classes/META-INF/spring-configuration-metadata.json`
- Metadata cau hinh do Spring Boot annotation processor tao ra.
- IDE va tooling co the dung file nay de goi y config key.

#### `target/maven-archiver`

`/target/maven-archiver/pom.properties`
- Metadata Maven sinh ra khi package artifact.
- Thuong chua groupId, artifactId, version.

#### `target/maven-status`

`/target/maven-status/maven-compiler-plugin/compile/default-compile/createdFiles.lst`
- Danh sach file `.class` da duoc tao boi Maven compiler plugin.

`/target/maven-status/maven-compiler-plugin/compile/default-compile/inputFiles.lst`
- Danh sach file `.java` dau vao duoc Maven compiler plugin dung de compile.

## 4. Luong chay nghiep vu thuc te

### 4.1 Luc app khoi dong

1. Chay `mvnw` hoac `mvnw.cmd`.
2. `MainApplication` startup Spring Boot.
3. `DefaultProfileUtil` dat profile `dev` neu chua co.
4. `PersistenceConfiguration` tao datasource Hikari.
5. Flyway chay `V1__init_schema.sql`.
6. Security chain va JWT filter duoc dang ky.

### 4.2 Luc user login

1. Client goi `POST /login`.
2. `AuthController` nhan `LoginRequest`.
3. `AuthenticationManager` goi `UserDetailsServiceImpl`.
4. `UserDetailsServiceImpl` dung `EmployeeRepository` tim user theo `employee_login_id`.
5. Spring Security so khop password hash BCrypt.
6. `JwtTokenProvider` tao JWT.
7. `LoginResponse` tra `accessToken` ve client.

### 4.3 Luc goi API da bao ve

1. Client gui `Authorization: Bearer <token>`.
2. `JwtTokenFilter` doc token.
3. `JwtTokenProvider` verify token.
4. `UserDetailsServiceImpl` nap lai user.
5. Security dat authentication vao context.
6. Controller private moi duoc chay.

## 5. Nhan xet nhanh ve hien trang code

Project hien tai la mot bo khung backend auth JWT co quy mo nho, chua co service layer nghiep vu hoan chinh. Mot so diem can luu y khi doc code:

- Chua co thu muc `service` du dung nhu README mo ta.
- `EmployeeMapper` co khai bao method co ve sai kieu tra ve.
- Entity `Employee` chua map cot `department_id` du co trong SQL.
- `target` va `.idea` dang nam trong project hien tai; day la file build/IDE, khong phai logic backend cot loi.

Neu can, buoc tiep theo hop ly la tao them 1 tai lieu ngan hon theo kieu "so do kien truc + flow request + dependency map" de doc nhanh hon file nay.
