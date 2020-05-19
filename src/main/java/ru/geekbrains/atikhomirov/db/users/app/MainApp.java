package ru.geekbrains.atikhomirov.db.users.app;

import java.util.ArrayList;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        try {
            DbUsers.connect();

            int count = 10;
            List<String> usersData = genUsersData(count);
            long time = System.currentTimeMillis();
            DbUsers.add(usersData);
            System.out.printf("Added %d users. Time: %d\n\n", count, System.currentTimeMillis() - time);

            System.out.printf("All users:\n%s\n", DbUsers.getAllUsers());

            int min = 40;
            int max = 60;
            System.out.printf("Users with age > %d and age < %d:\n%s\n", min, max, DbUsers.getUsersByAge(min, max));

            DbUsers.delUserByName("User3");
            System.out.printf("Deleted user with name: %s\n%s\n", "User3", DbUsers.getAllUsers());

            List<String> names = genUserNames(4, 8);
            time = System.currentTimeMillis();
            DbUsers.delUserByName(names);
            System.out.printf("Time: %d. Deleted users with names:\n%s\n%s\n", System.currentTimeMillis() - time,  names, DbUsers.getAllUsers());
        } finally {
            DbUsers.disconnect();
        }
    }

    private static List<String> genUsersData(int count) {
        StringBuilder sb;
        List<String> usersData = new ArrayList<>();

        for (int i = 1; i <= count; ++i) {
            sb = new StringBuilder();
            sb.append("User").append(i).append(" ");
            sb.append((i * 10)%50 + 25).append(" ");
            sb.append("user").append(i).append("@user-mail.com");
            usersData.add(sb.toString());
        }

        return usersData;
    }

    private static List<String> genUserNames(int start, int end) {
        StringBuilder sb;
        List<String> userNames = new ArrayList<>();

        for (int i = start; i <= end; ++i) {
            sb = new StringBuilder();
            sb.append("User").append(i);
            userNames.add(sb.toString());
        }

        return userNames;
    }
}
