/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

/**
 *
 * @author netdong
 */
public interface SecureTask<B, S, T> {
    B execute(S s, T t);
}
