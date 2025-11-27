package com.ch220048.eventcenter.data.model

// Licencias Creative Commons disponibles
enum class CCLicense(
    val codigo: String,
    val nombreCompleto: String,
    val descripcion: String,
    val iconoUrl: String
) {
    CC_BY(
        codigo = "CC BY",
        nombreCompleto = "Atribución",
        descripcion = "Permite a otros distribuir, mezclar, ajustar y construir a partir de tu obra, incluso comercialmente, siempre que te den crédito por la creación original.",
        iconoUrl = "https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by.png"
    ),
    CC_BY_SA(
        codigo = "CC BY-SA",
        nombreCompleto = "Atribución-CompartirIgual",
        descripcion = "Permite a otros mezclar, ajustar y construir a partir de tu obra, incluso comercialmente, siempre que te den crédito y licencien sus nuevas obras bajo condiciones idénticas.",
        iconoUrl = "https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by-sa.png"
    ),
    CC_BY_ND(
        codigo = "CC BY-ND",
        nombreCompleto = "Atribución-SinDerivadas",
        descripcion = "Permite la redistribución comercial y no comercial, siempre y cuando la obra se transmita íntegra y sin cambios, dándote crédito.",
        iconoUrl = "https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by-nd.png"
    ),
    CC_BY_NC(
        codigo = "CC BY-NC",
        nombreCompleto = "Atribución-NoComercial",
        descripcion = "Permite a otros mezclar, ajustar y construir a partir de tu obra de manera no comercial, y aunque sus nuevas obras deben reconocerte y ser no comerciales, no tienen que licenciarse bajo las mismas condiciones.",
        iconoUrl = "https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by-nc.png"
    ),
    CC_BY_NC_SA(
        codigo = "CC BY-NC-SA",
        nombreCompleto = "Atribución-NoComercial-CompartirIgual",
        descripcion = "Permite a otros mezclar, ajustar y construir a partir de tu obra de manera no comercial, siempre que te den crédito y licencien sus nuevas obras bajo condiciones idénticas.",
        iconoUrl = "https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by-nc-sa.png"
    ),
    CC_BY_NC_ND(
        codigo = "CC BY-NC-ND",
        nombreCompleto = "Atribución-NoComercial-SinDerivadas",
        descripcion = "Es la más restrictiva. Solo permite que otros puedan descargar las obras y compartirlas con otras personas, siempre que te den crédito, pero no se pueden cambiar de ninguna manera ni se pueden utilizar comercialmente.",
        iconoUrl = "https://mirrors.creativecommons.org/presskit/buttons/88x31/png/by-nc-nd.png"
    );

    companion object {
        fun fromCodigo(codigo: String): CCLicense {
            return values().find { it.codigo == codigo } ?: CC_BY
        }
    }
}