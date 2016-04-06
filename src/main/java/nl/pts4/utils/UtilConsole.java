package nl.pts4.utils;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.security.HashConstants;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class UtilConsole {
    public static void util(String[] args) throws Exception {
        System.out.println("Various quick tools for testing");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Choose a utility \n* HASH\n* UUID");
        String input = reader.readLine();

        if (input.trim().toLowerCase().equals("hash")) {
            System.out.println("For testing purposes, generate a hash here");

            System.out.println("Enter a password to hash");
            String password = reader.readLine();

            System.out.println("Enter amount of iterations (default 1)");
            String iterationsString = reader.readLine();

            if (Objects.equals(iterationsString, "")) iterationsString = "1";
            int iteration = Integer.parseInt(iterationsString);

            System.out.println("Result hash(es):");
            for (int i = 0; i < iteration; i++) {
                String hash = SCryptUtil.scrypt(password, HashConstants.N, HashConstants.r, HashConstants.p);
                System.out.println(hash);
            }
        }
        if (input.trim().toLowerCase().equals("uuid")) {
            System.out.println("Here is a random UUID, it's also on your clipboard now");
            UUID uuid = UUID.randomUUID();

            System.out.println(uuid);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(uuid.toString()), null);
        } else {
            System.out.println("oops that's not a valid option");
            util(args);
        }


    }
}
