package ru.itis.listeners;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.itis.repository.*;
import ru.itis.services.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.nio.file.Paths;

@WebListener
public class CustomServletContextListener implements ServletContextListener {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/druzhba_cup";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "IV2007@$";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);

        UsersRepository usersRepository = new UsersRepositoryJdbcImpl(dataSource);
        MatchesRepository matchesRepository = new MatchesRepositoryJdbcImpl(dataSource);
        SeasonsRepository seasonsRepository = new SeasonsRepositoryJdbcImpl(dataSource);
        TournamentsRepository tournamentsRepository = new TournamentsRepositoryJdbcImpl(dataSource);
        TeamsRepository teamsRepository = new TeamsRepositoryJdbcImpl(dataSource);
        FilesRepository filesRepository = new FilesRepositoryJdbcImpl(dataSource);
        VenuesRepository venuesRepository = new VenuesRepositoryJdbcImpl(dataSource);
        PlayersRepository playersRepository = new PlayersRepositoryJdbcImpl(dataSource);
        PlayerTeamPeriodsRepository periodsRepository = new PlayerTeamPeriodsRepositoryJdbcImpl(dataSource);
        MatchRostersRepository rostersRepository = new MatchRostersRepositoryJdbcImpl(dataSource);
        GoalsRepository goalsRepository = new GoalsRepositoryJdbcImpl(dataSource);

        SignInService signInService = new SignInServiceImpl(usersRepository);
        SignUpService signUpService = new SignUpServiceImpl(usersRepository, 1);
        MatchesService matchesService = new MatchesServiceImpl(matchesRepository, teamsRepository, tournamentsRepository, venuesRepository, goalsRepository, rostersRepository);
        SeasonsService seasonsService = new SeasonsServiceImpl(seasonsRepository);
        TournamentsService tournamentsService = new TournamentsServiceImpl(tournamentsRepository);
        TeamsService teamsService = new TeamsServiceImpl(teamsRepository);
        StandingsService standingsService = new StandingsServiceImpl();
        FileService fileService = new FileServiceImpl(filesRepository, Paths.get("C:/files"));
        VenuesService venuesService = new VenuesServiceImpl(venuesRepository);
        PlayersService playersService = new PlayersServiceImpl(playersRepository);
        PlayerTeamPeriodsService periodsService = new PlayerTeamPeriodsServiceImpl(periodsRepository, playersRepository, teamsRepository);
        MatchRostersService rostersService = new MatchRostersServiceImpl(rostersRepository, matchesRepository, playersRepository);
        GoalsService goalsService = new GoalsServiceImpl(goalsRepository, matchesRepository, rostersRepository);

        servletContext.setAttribute("signInService", signInService);
        servletContext.setAttribute("signUpService", signUpService);
        servletContext.setAttribute("matchesService", matchesService);
        servletContext.setAttribute("seasonsService", seasonsService);
        servletContext.setAttribute("tournamentsService", tournamentsService);
        servletContext.setAttribute("teamsService", teamsService);
        servletContext.setAttribute("standingsService", standingsService);
        servletContext.setAttribute("fileService", fileService);
        servletContext.setAttribute("venuesService", venuesService);
        servletContext.setAttribute("playersService", playersService);
        servletContext.setAttribute("playerTeamPeriodsService", periodsService);
        servletContext.setAttribute("matchRostersService", rostersService);
        servletContext.setAttribute("goalsService", goalsService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {


    }
}
