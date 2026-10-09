package jobs;

import models.Login;
import play.jobs.OnApplicationStart;
import play.jobs.Job;

@OnApplicationStart
public class Bootstrap extends Job {
    
    public void doJob() {
    if (Login.count() == 0) {
        Login admin = new Login();
        admin.login = "Admin";
        admin.senha = "Admin1234";
        admin.perfil = "ADMIN";
        admin.nome = "ADM";
        admin.save();

        Login comum = new Login();
        comum.login = "Usuario";
        comum.senha = "Usuario1234";
        comum.perfil = "USUARIO";
        comum.nome = "Atendente";
        comum.save();
    }
}
}