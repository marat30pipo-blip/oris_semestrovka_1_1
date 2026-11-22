package ru.itis.services;

import ru.itis.dto.MatchDto;
import ru.itis.dto.StandingDto;
import ru.itis.models.Team;

import java.util.ArrayList;
import java.util.List;

public class StandingsServiceImpl implements StandingsService{
    @Override
    public List<StandingDto> calculate(List<Team> teams, List<MatchDto> matches) {
        List<StandingDto> standings = calculateStatistics(teams, matches);

        int start = 0;

        while (start < standings.size()) {
            int end = start + 1;
            StandingDto first = standings.get(start);

            while (end < standings.size()) {
                StandingDto next = standings.get(end);

                if (first.getPoints() == next.getPoints() && first.getGoalDifference() == next.getGoalDifference()
                && first.getGoalsFor() == next.getGoalsFor()) {
                    end++;
                } else {
                    break;
                }
            }
            if (end - start > 1) {
                List<Team> groupTeams = new ArrayList<>();

                for (int i = start; i < end ; i++) {
                    StandingDto standing = standings.get(i);

                    for (Team team : teams) {
                        if (team.getTeamId() == standing.getTeamId()) {
                            groupTeams.add(team);
                            break;
                        }
                    }
                }
                List<MatchDto> groupMatches = new ArrayList<>();

                for (MatchDto match : matches) {
                    boolean firstTeamInGroup = false;
                    boolean secondTeamInGroup = false;

                    for (Team team : groupTeams) {
                        if (team.getTeamId() == match.getTeam1Id()) {
                            firstTeamInGroup = true;
                        }

                        if (team.getTeamId() == match.getTeam2Id()) {
                            secondTeamInGroup = true;
                        }
                    }

                    if (firstTeamInGroup && secondTeamInGroup) {
                        groupMatches.add(match);
                    }
                }
                List<StandingDto> personalStandings = calculateStatistics(groupTeams, groupMatches);

                List<StandingDto> orderedGroup = new ArrayList<>();

                for (StandingDto personalStanding : personalStandings) {
                    for (int i = start; i < end; i++) {
                        StandingDto standing = standings.get(i);

                        if (standing.getTeamId() == personalStanding.getTeamId()) {
                            orderedGroup.add(standing);
                            break;
                        }
                    }
                }

                for (int i = 0; i < orderedGroup.size(); i++) {
                    standings.set(start + i, orderedGroup.get(i));
                }

            }
            start = end;
        }

        return standings;
    }

    private List<StandingDto> calculateStatistics(List<Team> teams, List<MatchDto> matches) {
        List<StandingDto> standings = new ArrayList<>();

        for (Team team : teams) {
            StandingDto standing = new StandingDto();
            standing.setTeamId(team.getTeamId());
            standing.setTeamName(team.getName());

            standings.add(standing);
        }

        for (MatchDto match : matches) {
            if (!"Завершён".equals(match.getStatus()) && !"Технический результат".equals(match.getStatus())) {
                continue;
            }

            if (match.getScore1() == null || match.getScore2() == null) {
                throw new IllegalStateException( "У завершённого матча отсутствует счёт: " + match.getMatchId());
            }

            int score1 = match.getScore1();
            int score2 = match.getScore2();

            for (StandingDto standing : standings) {
                if (standing.getTeamId() == match.getTeam1Id()) {
                    standing.setPlayed(standing.getPlayed() + 1);
                    standing.setGoalsFor(standing.getGoalsFor() + score1);
                    standing.setGoalsAgainst(standing.getGoalsAgainst() + score2);

                    if (score1 > score2) {
                        standing.setWins(standing.getWins() + 1);
                        standing.setPoints(standing.getPoints() + 3);
                    } else if (score1 == score2) {
                        standing.setDraws(standing.getDraws() + 1);
                        standing.setPoints(standing.getPoints() + 1);
                    } else {
                        standing.setLosses(standing.getLosses() + 1);
                    }

                } else if (standing.getTeamId() == match.getTeam2Id()) {
                    standing.setPlayed(standing.getPlayed() + 1);
                    standing.setGoalsFor(standing.getGoalsFor() + score2);
                    standing.setGoalsAgainst(standing.getGoalsAgainst() + score1);

                    if (score2 > score1) {
                        standing.setWins(standing.getWins() + 1);
                        standing.setPoints(standing.getPoints() + 3);
                    } else if (score1 == score2) {
                        standing.setDraws(standing.getDraws() + 1);
                        standing.setPoints(standing.getPoints() + 1);
                    } else {
                        standing.setLosses(standing.getLosses() + 1);
                    }
                }
            }
        }

        for (int i = 0; i < standings.size() - 1; i++) {
            for (int j = i+1; j < standings.size(); j++) {
                StandingDto first = standings.get(i);
                StandingDto second = standings.get(j);

                boolean secondIsBetter = false;

                if (second.getPoints() > first.getPoints()) {
                    secondIsBetter = true;
                } else if (second.getPoints() == first.getPoints()) {
                    if (second.getGoalDifference() > first.getGoalDifference()) {
                        secondIsBetter = true;
                    }
                    else if (second.getGoalDifference() == first.getGoalDifference()) {
                        if (second.getGoalsFor() > first.getGoalsFor()) {
                            secondIsBetter = true;
                        } else if (second.getGoalsFor() == first.getGoalsFor()) {
                            int nameComparison = second.getTeamName().compareToIgnoreCase(first.getTeamName());

                            if (nameComparison < 0) {
                                secondIsBetter = true;
                            } else if (nameComparison == 0 && second.getTeamId() < first.getTeamId()) {
                                secondIsBetter = true;
                            }
                        }
                    }
                }
                if (secondIsBetter) {
                    standings.set(i, second);
                    standings.set(j, first);
                }
            }
        }

        return standings;
    }
}
