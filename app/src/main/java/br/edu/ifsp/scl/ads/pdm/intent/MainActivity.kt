package br.edu.ifsp.scl.ads.pdm.intent

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_INTENT
import android.content.Intent.EXTRA_TITLE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.scl.ads.pdm.intent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object Constantes {
        const val PARAMETRO_EXTRA = "PARAMETRO_EXTRA"
    }

    private lateinit var parl: ActivityResultLauncher<Intent>
    private lateinit var pcarl: ActivityResultLauncher<String>
    private lateinit var piarl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(amb.root)

        setSupportActionBar(amb.toolbarTb)
        supportActionBar?.apply {
            title = getString(R.string.app_name)
            subtitle = this@MainActivity.javaClass.simpleName
        }

        parl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getStringExtra(PARAMETRO_EXTRA)?.let {
                    amb.parametroTv.text = it
                }
            }
        }

        piarl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                resultado.data?.data?.let {
                    startActivity(Intent(ACTION_VIEW, it))
                }
            }
        }

        pcarl = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { permissaoConcedida ->
            if (permissaoConcedida) {
                chamarOuDiscar(true)
            } else {
                Toast.makeText(this, "Permissão necessária!", Toast.LENGTH_SHORT).show()
            }

        }

        amb.entrarParametroBt.setOnClickListener{
            Intent ("MINHA_ACTION").apply {
                amb.parametroTv.text.toString().let{
                    putExtra(PARAMETRO_EXTRA, it)
                }
                parl.launch(this)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.viewMi -> {
                val url: Uri = Uri.parse(amb.parametroTv.text.toString())
                val navegadorIntent = Intent(ACTION_VIEW, url)
                startActivity(navegadorIntent)
                true
            }
            R.id.callMi -> {
                if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                    chamarOuDiscar(true)
                } else {
                    pcarl.launch(CALL_PHONE)
                }
                true
            }
            R.id.dialMi -> {
                chamarOuDiscar(chamar = false)
                true
            }
            R.id.pickMi -> {
                val caminho = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                val pegarImagenIntent = Intent(ACTION_PICK)
                pegarImagenIntent.setDataAndType(Uri.parse(caminho), "image/*")
                piarl.launch(pegarImagenIntent)
                true
            }
            R.id.chooserMi -> {
                Uri.parse(amb.parametroTv.text.toString()).let {
                    Intent(ACTION_VIEW, it).let { navegadorIntent ->
                        val escolherAppIntent = Intent(ACTION_CHOOSER)
                        escolherAppIntent.putExtra(EXTRA_TITLE, "Escolha o seu navegador")
                        escolherAppIntent.putExtra(EXTRA_INTENT, navegadorIntent)
                        startActivity(escolherAppIntent)
                    }
                }
                true
            }
            else -> { false }
        }
    }

    private fun chamarOuDiscar (chamar: Boolean) {
        Uri.parse("tel: ${amb.parametroTv.text}").let {
            Intent(if (chamar) ACTION_CALL else ACTION_DIAL).apply {
                data = it
                startActivity(this)
            }
        }
    }
}