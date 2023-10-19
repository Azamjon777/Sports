package aplicativo.para.esportes.game

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import aplicativo.para.esportes.R
import aplicativo.para.esportes.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clicks()

        viewModel.shuffleQuestions()
        updateUI()
    }

    private fun clicks() {
        binding.yesButton.setOnClickListener {
            if (viewModel.isBetPlaced()) {
                handleAnswer(GameViewModel.AnswerType.YES)
            } else {
                Toast.makeText(this, "Enter coins before answer", Toast.LENGTH_SHORT).show()
            }
        }

        binding.noButton.setOnClickListener {
            if (viewModel.isBetPlaced()) {
                handleAnswer(GameViewModel.AnswerType.NO)
            } else {
                Toast.makeText(this, "Enter coins before answer", Toast.LENGTH_SHORT).show()
            }
        }
        binding.resetCount.setOnClickListener {
            viewModel.resetCoinsTo5000()
            updateUI()
        }

        binding.rules.setOnClickListener {
            val alertDialogBuilder =
                AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Rules")
            alertDialogBuilder.setMessage(getString(R.string.rules))

            // Кнопка "OK" для закрытия диалога
            alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

            // Показываем диалог
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        binding.submitBetButton.setOnClickListener {
            val betText = binding.betEditText.text.toString()
            if (betText.isNotEmpty()) {
                val bet = betText.toInt()
                if (viewModel.setPlayerBet(bet)) {
                    viewModel.coins.value = viewModel.coins.value?.minus(betText.toInt())
                    binding.betEditText.isEnabled = false
                    binding.submitBetButton.isEnabled = false
                    updateUI()
                } else {
                    Toast.makeText(
                        this,
                        "You don't have enough coins",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Enter the number of coins", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleAnswer(answer: GameViewModel.AnswerType) {
        if (!viewModel.isBetPlaced()) {
            Toast.makeText(this, "Place a bet before the call", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.answerQuestion(answer)
        updateUI()

        if (viewModel.getAnsweredQuestionsCount() == viewModel.getTotalQuestionsCount()) {
            // Показываем диалог с результатами после ответа на 15 вопросов
            val resultMessage = if (viewModel.isGameWon()) {
                "Congratulations! You have won! Your coins have been doubled. You answered all 15 questions correctly."
            } else {
                "You answered ${viewModel.getCorrectlyAnsweredCount()} out of ${viewModel.getTotalQuestionsCount()}questions correctly"
            }

            if (viewModel.isGameWon()) {
                viewModel.doubleBetAndAddToCoins() // Удвоить монеты, если игра выиграна
            }

            AlertDialog.Builder(this)
                .setTitle("Result")
                .setMessage(resultMessage)
                .setCancelable(false)
                .setPositiveButton("Restart") { _, _ ->
                    viewModel.restartGame()
                    binding.betEditText.isEnabled = true
                    binding.submitBetButton.isEnabled = true
                    viewModel.shuffleQuestions() // Перемешиваем вопросы перед новой игрой
                    updateUI()
                }
                .show()
        }
    }


    private fun updateUI() {
        val coinsText = String.format("Coins: %d", viewModel.getCoins())
        val currentBetText = String.format("Inserted points : %d", viewModel.getCurrentBet())

        binding.questionTextView.text = viewModel.getCurrentQuestion()
        binding.coinsTextView.text = coinsText
        binding.currentBetTextView.text = currentBetText
    }
}