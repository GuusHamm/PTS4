package nl.pts4.security;


import com.lambdaworks.crypto.SCryptUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
        for (int i = 0; i < iteration; i++) {
            String hash = SCryptUtil.scrypt(password, HashConstants.N, HashConstants.r, HashConstants.p);
            System.out.println(hash);
        }
    }

}
