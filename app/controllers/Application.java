package controllers;

import play.*;
import play.mvc.*;
import java.util.*;
import models.*;

@With (Autenticador.class)
public class Application extends Controller {

    public static void index() {
        render();
    }

}