package nl.pts4.security;


import com.lambdaworks.crypto.SCryptUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Teun
 */
public class HashConsole {

    public static void main(String[] args) throws Exception {
        System.out.println("For testing purposes, generate a hash here");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter a password to hash");
        String password = reader.readLine();

        System.out.println("Enter amount of iterations (default 1)");
        String iterationsString = reader.readLine();

        if (Objects.equals(iterationsString, "")) iterationsString = "1";
        int iteration = Integer.parseInt(iterationsString);

        System.out.println("Result hash(es):");
        List<String> hashes = new ArrayList<>(iteration);
        for (int i = 0; i < iteration; i++) {
            String hash = SCryptUtil.scrypt(password, HashConstants.N, HashConstants.r, HashConstants.p);
            hashes.add(hash);
            System.out.println(hash);
        }

        System.out.println("Verifying...");
        for (String hash : hashes) {
            boolean s = SCryptUtil.check(password, hash);
            if (!s) {
                System.out.println("Failed the check");
            }
        }
        System.out.println("Done");
    }

}
