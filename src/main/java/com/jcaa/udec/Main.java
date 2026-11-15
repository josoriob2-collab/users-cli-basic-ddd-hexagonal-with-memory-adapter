package com.jcaa.udec;

import com.jcaa.udec.collections.adapter.persistence.memory.GuardarUsuarioAdapter;
import com.jcaa.udec.collections.adapter.persistence.memory.ObtenerUsuariosAdapter;
import com.jcaa.udec.collections.adapter.persistence.postgres.ActualizarRadioPostgresAdapter;
import com.jcaa.udec.collections.adapter.persistence.postgres.ConexionPostgres;
import com.jcaa.udec.collections.adapter.persistence.postgres.EliminarRadioPostgresAdapter;
import com.jcaa.udec.collections.adapter.persistence.postgres.GuardarRadioPostgresAdapter;
import com.jcaa.udec.collections.adapter.persistence.postgres.ObtenerRadiosPostgresAdapter;
import com.jcaa.udec.collections.application.service.ActualizarRadioService;
import com.jcaa.udec.collections.application.service.AgregarRadioService;
import com.jcaa.udec.collections.application.service.AgregarUsuarioService;
import com.jcaa.udec.collections.application.service.EliminarRadioService;
import com.jcaa.udec.collections.application.service.ObtenerRadiosService;
import com.jcaa.udec.collections.application.service.ObtenerUsuariosService;
import com.jcaa.udec.collections.application.service.ports.in.ActualizarRadioUseCase;
import com.jcaa.udec.collections.application.service.ports.in.AgregarRadioUseCase;
import com.jcaa.udec.collections.application.service.ports.in.AgregarUsuarioUseCase;
import com.jcaa.udec.collections.application.service.ports.in.EliminarRadioUseCase;
import com.jcaa.udec.collections.application.service.ports.in.ObtenerRadioUseCase;
import com.jcaa.udec.collections.application.service.ports.in.ObtenerUsuarioUseCase;
import com.jcaa.udec.collections.domain.port.out.ActualizarRadioPort;
import com.jcaa.udec.collections.domain.port.out.EliminarRadioPort;
import com.jcaa.udec.collections.domain.port.out.GuardarRadioPort;
import com.jcaa.udec.collections.domain.port.out.GuardarUsuarioPort;
import com.jcaa.udec.collections.domain.port.out.ObtenerRadiosPort;
import com.jcaa.udec.collections.domain.port.out.ObtenerUsuariosPort;
import com.jcaa.udec.collections.entrypoint.cli.GuiCli;
import com.jcaa.udec.collections.entrypoint.controller.RadioControlador;
import com.jcaa.udec.collections.entrypoint.controller.RadioControladorImpl;
import com.jcaa.udec.collections.entrypoint.controller.UsuarioControlador;
import com.jcaa.udec.collections.entrypoint.controller.UsuarioControladorImpl;

public class  Main {
    public static void main(String[] args) {
        GuardarUsuarioPort guardarUsuarioPort = new GuardarUsuarioAdapter();
        ObtenerUsuariosPort obtenerUsuariosPort = new ObtenerUsuariosAdapter();
        AgregarUsuarioUseCase agregarUsuarioUseCase = new AgregarUsuarioService(guardarUsuarioPort);
        ObtenerUsuarioUseCase obtenerUsuarioUseCase = new ObtenerUsuariosService(obtenerUsuariosPort);
        UsuarioControlador usuarioControlador =
                new UsuarioControladorImpl(agregarUsuarioUseCase, obtenerUsuarioUseCase);

        ConexionPostgres conexionPostgres = ConexionPostgres.desdeEntorno();
        GuardarRadioPort guardarRadioPort = new GuardarRadioPostgresAdapter(conexionPostgres);
        ObtenerRadiosPort obtenerRadiosPort = new ObtenerRadiosPostgresAdapter(conexionPostgres);
        ActualizarRadioPort actualizarRadioPort = new ActualizarRadioPostgresAdapter(conexionPostgres);
        EliminarRadioPort eliminarRadioPort = new EliminarRadioPostgresAdapter(conexionPostgres);
        AgregarRadioUseCase agregarRadioUseCase = new AgregarRadioService(guardarRadioPort);
        ObtenerRadioUseCase obtenerRadioUseCase = new ObtenerRadiosService(obtenerRadiosPort);
        ActualizarRadioUseCase actualizarRadioUseCase = new ActualizarRadioService(actualizarRadioPort);
        EliminarRadioUseCase eliminarRadioUseCase = new EliminarRadioService(eliminarRadioPort);
        RadioControlador radioControlador = new RadioControladorImpl(
                agregarRadioUseCase, obtenerRadioUseCase, actualizarRadioUseCase, eliminarRadioUseCase);

        GuiCli guiCli = new GuiCli(usuarioControlador, radioControlador);
        guiCli.ejecutarAccion();
    }
}
