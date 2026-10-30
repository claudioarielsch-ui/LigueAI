package com.ligueai.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import android.graphics.Typeface

class MainActivity : Activity() {

    private var modo = "Estratega"
    private var objetivo = "Generar atracción"
    private var picardia = "Variable"
    private var captura: String? = null

    private val colores = mapOf(
        "fondo" to Color.rgb(20, 20, 25),
        "dorado" to Color.rgb(232, 180, 75),
        "blanco" to Color.WHITE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(20, 20, 25)
        window.navigationBarColor = Color.rgb(20, 20, 25)
        construirPantalla()
    }

    private fun construirPantalla() {
        val fondo = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 35, 28, 24)
            setBackgroundColor(colores.getValue("fondo"))
        }

        val scroll = ScrollView(this)
        val contenido = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, 30)
        }

        contenido.addView(texto("LIGUE AI", 32, true, colores.getValue("dorado")))
        contenido.addView(texto("Tu estratega personal de conversaciones", 15, false, Color.LTGRAY))
        contenido.addView(espacio(28))

        contenido.addView(texto("01  ELEGÍ TU MODO", 17, true, Color.WHITE))
        contenido.addView(selector(
            listOf("Estratega", "Directo", "Divertido", "Romántico"),
            "Estratega"
        ) { modo = it })

        contenido.addView(espacio(20))
        contenido.addView(texto("02  ¿QUÉ BUSCÁS?", 17, true, Color.WHITE))
        contenido.addView(selector(
            listOf("Generar atracción", "Mantener interés", "Conquistar", "Responder con humor"),
            "Generar atracción"
        ) { objetivo = it })

        contenido.addView(espacio(20))
        contenido.addView(texto("03  NIVEL DE PICARDÍA", 17, true, Color.WHITE))
        contenido.addView(selector(
            listOf("Variable", "Sutil", "Atrevida"),
            "Variable"
        ) { picardia = it })

        contenido.addView(espacio(25))

        val botonImagen = boton("📷  SELECCIONAR CAPTURA")
        botonImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 100)
        }
        contenido.addView(botonImagen)

        contenido.addView(espacio(15))
        contenido.addView(texto("También podés contarme qué está pasando:", 14, false, Color.LTGRAY))

        val contexto = EditText(this).apply {
            hint = "Ej.: Me respondió después de dos días..."
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            setPadding(16, 16, 16, 16)
            minLines = 3
            gravity = Gravity.TOP
            setBackgroundColor(Color.rgb(42, 42, 49))
        }
        contenido.addView(contexto)

        contenido.addView(espacio(24))
        contenido.addView(boton("ANALIZAR CON CHATGPT").apply {
            setOnClickListener {
                val prompt = """
Sos Ligue AI, mi estratega personal para conversaciones de conquista.
Analizá la captura o el contexto que te comparto.
Modo: $modo
Objetivo: $objetivo
Picardía: $picardia

Respondé en español rioplatense natural.
No supongas intenciones que no estén demostradas.
Dame exactamente 3 respuestas listas para copiar:
1. Sutil y natural.
2. Coqueta y divertida.
3. Más atrevida, solo si hay reciprocidad.
Recomendá la mejor y explicá brevemente por qué.
Contexto: ${contexto.text}
""".trimIndent()

                val compartir = Intent(Intent.ACTION_SEND).apply {
                    type = if (captura != null) "image/*" else "text/plain"
                    putExtra(Intent.EXTRA_TEXT, prompt)
                    captura?.let {
                        putExtra(Intent.EXTRA_STREAM, android.net.Uri.parse(it))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
                startActivity(Intent.createChooser(compartir, "Analizar con ChatGPT"))
            }
        })

        contenido.addView(espacio(20))
        contenido.addView(texto(
            "LIGUE AI  •  Tu próxima respuesta puede cambiarlo todo.",
            12, false, Color.GRAY
        ))

        scroll.addView(contenido)
        fondo.addView(scroll)
        setContentView(fondo)
    }

    private fun texto(
        valor: String,
        tamaño: Float,
        negrita: Boolean,
        color: Int
    ): TextView {
        return TextView(this).apply {
            text = valor
            textSize = tamaño
            setTextColor(color)
            if (negrita) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun espacio(alto: Int): TextView {
        return TextView(this).apply {
            height = alto
        }
    }

    private fun selector(
        opciones: List<String>,
        inicial: String,
        cambio: (String) -> Unit
    ): Spinner {
        val spinner = Spinner(this)
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            opciones
        )
        spinner.adapter = adaptador
        spinner.setSelection(opciones.indexOf(inicial))
        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                cambio(opciones[position])
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
        return spinner
    }

    private fun boton(titulo: String): Button {
        return Button(this).apply {
            text = titulo
            setTextColor(Color.BLACK)
            textSize = 15f
            setBackgroundColor(colores.getValue("dorado"))
            isAllCaps = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            captura = data?.data?.toString()
            Toast.makeText(this, "Captura seleccionada", Toast.LENGTH_SHORT).show()
        }
    }
}
