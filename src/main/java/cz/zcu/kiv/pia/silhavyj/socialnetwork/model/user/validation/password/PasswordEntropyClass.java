package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password;

/***
 * Password strength classes. These classes are determined by the result of
 * the entropy algorithm used when assessing user's password. As one of the assignment
 * requirements, very weak and weak passwords and not allowed to be user by any user.
 *
 * https://www.pleacher.com/mp/mlessons/algebra/entropy.html
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public enum PasswordEntropyClass {

    /*** very weak password (entropy value: < 28) */
    VERY_WEAK,

    /*** weak password (entropy value: <28; 35>) */
    WEAK,

    /*** reasonable password (entropy value: <36; 59>) */
    REASONABLE,

    /*** strong password (entropy value: <60; 127>) */
    STRONG,

    /*** very strong password (entropy value: >= 128) */
    VERY_STRONG
}
