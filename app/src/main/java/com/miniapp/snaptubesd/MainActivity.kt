package com.miniapp.snaptubesd

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnMover = findViewById<Button>(R.id.btnMover)
        val txtEstado = findViewById<TextView>(R.id.txtEstado)

        btnMover.setOnClickListener {
            txtEstado.text = "Moviendo archivos..."
            try {
                // Ruta interna fija de Snaptube
                val origen = File("/storage/emulated/0/Download/Snaptube/Download/Snaptube Audio/")
                
                // Buscar la SD externa en Android 6
                val listaStorage = File("/storage/").listFiles()
                val carpetaSD = listaStorage?.firstOrNull { it.name != "emulated" && it.name != "self" }

                if (carpetaSD == null) {
                    txtEstado.text = "Error: No se detectó la tarjeta SD."
                    return@setOnClickListener
                }

                val destino = File(carpetaSD, "MusicaSnaptube/")
                if (!destino.exists()) destino.mkdirs()

                if (origen.exists() && origen.isDirectory) {
                    val archivos = origen.listFiles { _, name -> name.endsWith(".mp3") }
                    if (archivos.isNullOrEmpty()) {
                        txtEstado.text = "No hay canciones nuevas para mover."
                        return@setOnClickListener
                    }

                    var cont = 0
                    for (f in archivos) {
                        val archivoDestino = File(destino, f.name)
                        FileInputStream(f).use { inStream ->
                            FileOutputStream(archivoDestino).use { outStream ->
                                inStream.copyTo(outStream)
                            }
                        }
                        f.delete()
                        cont++
                    }
                    txtEstado.text = "¡Éxito! Se movieron $cont canciones."
                } else {
                    txtEstado.text = "No se encontró la carpeta Snaptube."
                }
            } catch (e: Exception) {
                txtEstado.text = "Error: ${e.message}"
            }
        }
    }
}
