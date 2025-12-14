//package kr.co.ohgoodfood.global.config;
//
//import java.util.concurrent.Executor;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.transaction.TransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import com.zaxxer.hikari.HikariDataSource;
//
//import kr.co.ohgoodfood.util.LoginInterceptor;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableWebMvc
//@EnableTransactionManagement
//@EnableScheduling
//@ComponentScan(basePackages = "kr.co.ohgoodfood")
//@MapperScan(basePackages = "kr.co.ohgoodfood", annotationClass = Mapper.class)
//public class MvcConfig implements WebMvcConfigurer {
//
//	// db.properties에 있는 속성
//	@Value("${db.driver}")
//	private String driver;
//	@Value("${db.url}")
//	private String url;
//	@Value("${db.username}")
//	private String username;
//	@Value("${db.password}")
//	private String password;
//
//	@Override
//	public void configureViewResolvers(ViewResolverRegistry registry) {
//		registry.jsp("/WEB-INF/views/", ".jsp");
//	}
//
//	// configureDefaultServletHandling
//	// 스프링이 아닌 톰켓이 정적 리소스를 처리하도록 설정
//	// img,css,js 등
//	@Override
//	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer config) {
//		config.enable();
//	}
//
//	// 비지니스 로직이 없는 페이지의 경우
//	// addViewController의 url로 진입 시 setViewName으로 포워딩
//	// @Override
//	// public void addViewControllers(ViewControllerRegistry registry) {
//	// 	registry.addViewController("/").setViewName("/common/login");
//	// 	registry.addViewController("/home").setViewName("/common/login");
//	// }
//
//	// HikariCP
//	@Bean
//	public DataSource dataSource() {
//		HikariDataSource dataSource = new HikariDataSource();
//		dataSource.setDriverClassName(driver);
//		dataSource.setJdbcUrl(url); // ip는 바뀌어야함
//		dataSource.setUsername(username);
//		dataSource.setPassword(password);
//
//		// 한글 인코딩 설정
//		dataSource.addDataSourceProperty("useUnicode", "true");
//		dataSource.addDataSourceProperty("characterEncoding", "UTF-8");
//		dataSource.addDataSourceProperty("serverTimezone", "Asia/Seoul");
//
//		return dataSource;
//	}
//
//	// MyBatis
//	@Bean
//	public SqlSessionFactory sqlSessionFactory() throws Exception {
//		SqlSessionFactoryBean ssf = new SqlSessionFactoryBean();
//		ssf.setDataSource(dataSource()); // 의존성 주입
//
//		// MyBatis 설정
//		// org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
//		// config.setMapUnderscoreToCamelCase(true);
//		// config.setCallSettersOnNulls(true);
//		// ssf.setConfiguration(config);
//
//		// 핸들러 스캔 패키지
//		ssf.setTypeHandlersPackage("kr.co.ohgoodfood.util.handler");
//
//		return ssf.getObject();
//	}
//
//	// 트랜잭션 선언
//	@Bean
//	public TransactionManager tm() {
//		TransactionManager tm = new DataSourceTransactionManager(dataSource());
//		return tm;
//	}
//
//	// interceptor
//	@Bean
//	public LoginInterceptor li() {
//		return new LoginInterceptor();
//	}
//
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(li())
//		.addPathPatterns("/user/**", "/store/**")  // 특정 경로들에만 적용
//		.excludePathPatterns("/login", "/jointype", "/store/signup", "/user/signup", "/user/checkId", "/store/checkId","/admin/**");
//
//		/*
//		 * /student/** 모든 페이지
//		 *
//		 * 관리자의 경우 모둔 url에 체크가 필요하기에 전부로 걸고 로그인만 제외 .excludePathPatterns("경로") <-- 제외
//		 */
//	}
//
////	// file Upload
////	@Bean
////	public CommonsMultipartResolver multipartResolver() {
////		CommonsMultipartResolver cmr = new CommonsMultipartResolver();
////		cmr.setMaxUploadSize(1024 * 1024 * 5);
////		cmr.setDefaultEncoding("UTF-8");
////		return cmr;
////	}
//
//	// swagger
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/swagger-ui/**")
//				.addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
//				.resourceChain(false);
//	}
//
//	// properties 설정
//	@Bean
//	public static PropertyPlaceholderConfigurer propreties() {
//		PropertyPlaceholderConfigurer config = new PropertyPlaceholderConfigurer();
//		config.setLocations(new ClassPathResource("db.properties"));
//		return config;
//	}
//
//	// 스케쥴러 쓰레드 풀 설정
//	@Bean
//    public ThreadPoolTaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(10); // 원하는 스레드 수
//        scheduler.setThreadNamePrefix("my-scheduler-");
//        scheduler.setWaitForTasksToCompleteOnShutdown(true); // 종료 대기 여부
//        scheduler.setAwaitTerminationSeconds(30); // 대기 시간
//        return scheduler;
//    }
//
//	// 비동기 처리 쓰레드 풀 설정
//	// @Bean(name = "taskExecutor")
//    // public Executor taskExecutor() {
//    //     ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//    //     executor.setCorePoolSize(5);      // 기본 5개 스레드
//    //     executor.setMaxPoolSize(10);      // 최대 10개
//    //     executor.setQueueCapacity(100);   // 큐 대기 허용
//    //     executor.setThreadNamePrefix("Async-");
//    //     executor.initialize();
//    //     return executor;
//    // }
//}
