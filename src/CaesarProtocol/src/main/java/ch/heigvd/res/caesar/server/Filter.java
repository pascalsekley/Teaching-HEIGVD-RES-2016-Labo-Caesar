/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.caesar.server;

import ch.heigvd.res.caesar.protocol.Protocol;

/**
 *
 * @author Pascal Sekley & Annie Sandra
 */
public class Filter {

   public String encrypt(String chaine, int delta) {
      String encrypted = "";
      for (int i = 0; i < chaine.length(); ++i) {
         int c = chaine.charAt(i);
            if (Character.isUpperCase(c)){
                //26 letters of the alphabet so mod by 26
                c = c + (delta % 26);
                if (c > 'Z')
                c = c - 26;

            }
            else if (Character.isLowerCase(c)){
                c = c + (delta % 26);
                if (c > 'z')
                c = c - 26;
            }

            encrypted += (char) c;

      }
      return encrypted;
   }

   
   public String decrypt(String chaine, int delta){
        String decrypted = "";

        for(int i = 0; i < chaine.length(); i++){
            int c = chaine.charAt(i);
            if (Character.isUpperCase(c)){
                c = c - (delta % 26);
                if (c < 'A')
                c = c + 26;
           }

            else if (Character.isLowerCase(c)){
                c = c - (delta % 26);
                if (c < 'a')
                c = c + 26;
            }
            decrypted += (char) c;

        }
        return decrypted;
   }
}
