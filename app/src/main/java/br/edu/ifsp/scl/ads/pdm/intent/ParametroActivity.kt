package br.edu.ifsp.scl.ads.pdm.intent

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.scl.ads.pdm.intent.databinding.ActivityParametroBinding

class ParametroActivity : AppCompatActivity() {
    private  val apb: ActivityParametroBinding by lazy{
        ActivityParametroBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(apb.root)

    }
}